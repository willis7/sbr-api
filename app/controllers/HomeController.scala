package controllers

import javax.inject.Singleton

import play.api.mvc.{ Action, Results }
import io.swagger.annotations.Api

import swagger.HomeControllerSwagger

/**
 * HomeController
 * ----------------
 * Author: haqa
 * Date: 12 July 2017 - 09:25
 * Copyright (c) 2017  Office for National Statistics
 */

@Api("Utils")
@Singleton
class HomeController extends Results with HomeControllerSwagger {

  def swagger = Action { request =>
    val host = request.host
    Redirect(url = s"http://$host/assets/lib/swagger-ui/index.html", queryString = Map("url" -> Seq(s"http://$host/swagger.json")))
  }

  def preflight(all: String) = Action {
    Ok("")
  }

}
