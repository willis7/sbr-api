package swagger

import play.api.mvc.{ Action, AnyContent }
import io.swagger.annotations.{ Api, ApiOperation, ApiResponse, ApiResponses }

/**
 * HomeControllerSwagger
 * ----------------
 * Author: haqa
 * Date: 10 July 2017 - 09:25
 * Copyright (c) 2017  Office for National Statistics
 */

trait HomeControllerSwagger {

  //public api
  @ApiOperation(
    value = "Swagger Documentation",
    notes = "Documentation of API endpoints for Swagger",
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success - Displays swagger documentation.")
  ))
  def swagger: Action[AnyContent]

  //public api
  @ApiOperation(
    value = "Permissions method request",
    notes = "pre-flight is used for local OPTIONS requests that precede PUT/DELETE requests. " +
    "An empty Ok() response allows the actual PUT/DELETE request to be sent.",
    httpMethod = "OPTIONS"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success - Permission accepted with OK message"),
    new ApiResponse(code = 404, message = "Not Found - Root not Found"),
    new ApiResponse(code = 500, message = "Internal Server Error")
  )) // hack CORS
  def preflight(all: String): Action[AnyContent]

}
