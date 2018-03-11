package controllers

import javax.inject.Singleton

import play.api.http.ContentTypes
import play.api.mvc.{ Action, Results }
import org.joda.time.DateTime
import io.swagger.annotations.Api

import swagger.HealthControllerSwagger

/**
 * HealthController
 * ----------------
 * Author: haqa
 * Date: 10 July 2017 - 09:25
 * Copyright (c) 2017  Office for National Statistics
 */
@Api("Utils")
@Singleton
class HealthController extends Results with ContentTypes with HealthControllerSwagger {
  private[this] val startTime = System.currentTimeMillis()

  def health = Action {
    val uptimeInMillis = uptime()
    Ok(s"{Status: Ok, Uptime: ${uptimeInMillis}ms, Date and Time: " + new DateTime(startTime) + "}").as(JSON)
  }

  private def uptime(): Long = {
    val uptimeInMillis = System.currentTimeMillis() - startTime
    uptimeInMillis
  }

}
