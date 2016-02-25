package com.chariotsolutions.kafka

import akka.actor.Actor
import kafka.producer.ReactiveKafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import spray.json.DefaultJsonProtocol

import scala.util.Random

class MockDeviceDataProducer(kafkaProducer: ReactiveKafkaProducer[String, String]) extends Actor with DefaultJsonProtocol {

  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  context.system.scheduler.schedule(1 seconds, 50 milliseconds, self, Msg)

  override def receive: Receive = {
    case Msg ⇒

      import spray.json._
      implicit val fooMarshaller = jsonFormat2(DeviceData.apply)

      val deviceData = DeviceData(Random.nextInt(100).toString, Random.nextDouble() * 100)
      kafkaProducer.producer.send(new ProducerRecord(kafkaProducer.props.topic, deviceData.toJson.toString()))
  }

  case object Msg

}
