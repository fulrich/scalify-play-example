package controllers

import play.api.Logger

trait ApplicationLogging {
  private val applicationName: String = "application"
  val logger: Logger = Logger(applicationName)
}
