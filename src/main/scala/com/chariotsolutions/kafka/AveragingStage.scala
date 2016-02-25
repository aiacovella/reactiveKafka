package com.chariotsolutions.kafka

import akka.stream.{Attributes, Outlet, Inlet, FlowShape}
import akka.stream.stage.{OutHandler, InHandler, GraphStageLogic, GraphStage}
import com.softwaremill.react.kafka.KafkaMessages._
import com.softwaremill.react.kafka.ProducerMessage
import spray.json.DefaultJsonProtocol

class AveragingStage extends GraphStage[FlowShape[DeviceData, StringProducerMessage]] with DefaultJsonProtocol {
  import spray.json._
  implicit val deviceAverageMarshaller = jsonFormat2(DeviceAverage.apply)

  val in = Inlet[DeviceData]("Filter.in")
  val out = Outlet[StringProducerMessage]("Filter.out")

  val shape = FlowShape(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val elem = grab(in)
          val avg = DeviceAverage("foo", 654.32)
          val outgoingMessage = ProducerMessage(avg.toJson.toString())
          push(out, outgoingMessage)
        }
      })
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          pull(in)
        }
      })
    }

}
