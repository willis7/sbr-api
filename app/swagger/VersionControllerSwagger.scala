package swagger

import play.api.mvc.{ Action, AnyContent }
import io.swagger.annotations.{ Api, ApiOperation, ApiResponse, ApiResponses }

/**
 * VersionControllerSwagger
 * ----------------
 * Author: haqa
 * Date: 10 July 2017 - 09:25
 * Copyright (c) 2017  Office for National Statistics
 */

trait VersionControllerSwagger {

  // public api
  @ApiOperation(
    value = "Version List",
    notes = "Provides a full listing of all versions of software related tools - this can be found in the build file.",
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success - Displays a version list as json.")
  ))
  def version: Action[AnyContent]

}
