package com.chariotsolutions.kafka

import akka.stream._
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.stage._
import akka.actor.{Props, ActorSystem}
import akka.util.Timeout
import com.softwaremill.react.kafka._
import com.softwaremill.react.kafka.KafkaMessages.{StringProducerMessage, StringConsumerRecord}
import com.softwaremill.react.kafka.{ReactiveKafka, ProducerProperties, ConsumerProperties}
import kafka.producer.ReactiveKafkaProducer
import org.apache.kafka.common.serialization.{StringSerializer, StringDeserializer}
import org.reactivestreams.{Subscriber, Publisher}
import scala.collection.JavaConversions._
import scala.concurrent.Future
import spray.json._

object Main extends App {

  val system = ActorSystem("chariot-iot-read")

  new Main(system).run()

}

class Main(actorSystem: ActorSystem) extends DefaultJsonProtocol {
  import scala.collection.JavaConversions._
  import scala.concurrent.duration._
  implicit val deviceDataMarshaller = jsonFormat2(DeviceData.apply)

  implicit val timeout = Timeout(1000.millis)
  implicit val system = actorSystem

  implicit val ctx = system.dispatcher // TODO: need separate dispatchers

  val settings = Settings(system)

  def run() = {

    implicit val materializer = ActorMaterializer()

    val kafka = new ReactiveKafka()

    val deviceDataTopicProducer = ProducerProperties(
      bootstrapServers = "127.0.0.1:9092",
      topic = "devicedata",
      keySerializer = new StringSerializer(),
      valueSerializer = new StringSerializer()
    )

    val deviceAverageTopicConsumer = ConsumerProperties(
      bootstrapServers = "127.0.0.1:9092",
      topic = "deviceAverage",
      groupId = "deviceGroup",
      keyDeserializer = new StringDeserializer(),
      valueDeserializer = new StringDeserializer()
    )

    system.actorOf(Props(new MockDeviceDataProducer(new ReactiveKafkaProducer(deviceDataTopicProducer))))

    val deviceDataTopicConsumer: Publisher[StringConsumerRecord] = kafka.consume(ConsumerProperties(
      bootstrapServers = "localhost:9092",
      topic = "devicedata",
      groupId = "deviceGroup",
      valueDeserializer = new StringDeserializer()
    ))

    val deviceAverageTopicPublisher: Subscriber[StringProducerMessage] = kafka.publish(ProducerProperties(
      bootstrapServers = "localhost:9092",
      topic = "deviceAverage",
      valueSerializer = new StringSerializer()
    ))

    // Consumer of aggregated data
    Future {
      val consumer = ReactiveKafkaConsumer(deviceAverageTopicConsumer)

      while (true) {
        val records = consumer.consumer.poll(10000)
        records.iterator().foreach{consumerRecord ⇒
          println(">>>>>>>>>>>>>>>>>>>>>> Final Value: " + consumerRecord.value())
        }
      }
    }

    // Materialize a stream with a source that is a reader of a kafka
    // topic and a sink that is a writer of a kafka topic.

    Source.fromPublisher(deviceDataTopicConsumer).map{m =>
      // Convert
      m.value().toString.parseJson.convertTo[DeviceData]

    }.via(new AveragingStage())
      .to(Sink.fromSubscriber(deviceAverageTopicPublisher)).run()

  }
}

