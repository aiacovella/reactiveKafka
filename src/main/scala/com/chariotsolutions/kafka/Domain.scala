package com.chariotsolutions.kafka

case class DeviceData(deviceId: String, value: BigDecimal)
case class DeviceAverage(deviceId: String, tenMinuteAverage: BigDecimal)
