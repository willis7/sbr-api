package swagger

import play.api.mvc.{ Action, AnyContent }
import io.swagger.annotations.{ Api, ApiOperation, ApiResponse, ApiResponses }

/**
 * HealthControllerSwagger
 * ----------------
 * Author: haqa
 * Date: 10 July 2017 - 09:25
 * Copyright (c) 2017  Office for National Statistics
 */

trait HealthControllerSwagger {

  //public api
  @ApiOperation(
    value = "Application Health",
    notes = "Provides a json object containing minimal information on application live status and uptime.",
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success - Displays a json object of basic api health.")
  ))
  def health: Action[AnyContent]

}
