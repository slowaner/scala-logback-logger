package com.github.slowaner.scala.logger.logback.test

import org.scalatest.{FlatSpec, Matchers}
import org.slf4j.LoggerFactory

import com.github.slowaner.scala.logger.logback.Logger

class LoggerTest extends FlatSpec with Matchers {
  "Logger" should "load custom configuration" in {
    val customConfiguration = getClass.getResource("/com/github/slowaner/scala/logger/logback/test/logtest.xml")
    Logger.init(customConfiguration)
    val logger = LoggerFactory.getLogger(this.getClass)
    logger.info("Some Test INFO Message")
  }
}
