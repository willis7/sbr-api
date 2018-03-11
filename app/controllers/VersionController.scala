package controllers

import javax.inject.Singleton

import play.api.http.ContentTypes
import play.api.mvc.{ Action, Results }
import io.swagger.annotations.Api

import swagger.VersionControllerSwagger

/**
 * VersionController
 * ----------------
 * Author: haqa
 * Date: 10 July 2017 - 09:25
 * Copyright (c) 2017  Office for National Statistics
 */

@Api("Utils")
@Singleton
class VersionController extends Results with ContentTypes with VersionControllerSwagger {

  def version = Action {
    Ok(BuildInfo.toJson).as(JSON)
  }
}