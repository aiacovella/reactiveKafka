package com.chariotsolutions.kafka

import akka.actor.{Actor, ExtendedActorSystem, Extension, ExtensionKey}

import scala.collection.JavaConversions._
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._
import scala.reflect.internal.util.StringOps

object Settings extends ExtensionKey[Settings]

class Settings(system: ExtendedActorSystem) extends Extension {
  val config = system.settings.config


}

trait SettingsActor {
  this: Actor =>

  val settings: Settings =
    Settings(context.system)
}
