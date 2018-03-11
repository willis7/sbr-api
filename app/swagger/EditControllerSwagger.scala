package swagger

import play.api.mvc.{ Action, AnyContent }
import io.swagger.annotations._

/**
 * EditControllerSwagger
 * ----------------
 * Author: haqa
 * Date: 10 July 2017 - 09:25
 * Copyright (c) 2017  Office for National Statistics
 */

trait EditControllerSwagger {

  @ApiOperation(
    value = "Ok if edit is made",
    notes = "Invokes a method in sbr-hbase-connector to edit an Enterprise",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "POST"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, responseContainer = "JsValue", message = "Edit has been made successfully to Enterprise with id: [id]"),
    new ApiResponse(code = 400, responseContainer = "JsValue", message = "BadRequest -> id or edit json is invalid"),
    new ApiResponse(code = 500, responseContainer = "JsValue", message = "InternalServerError -> Unable to make edit")
  ))
  def editEnterprise(
    @ApiParam(value = "An Enterprise ID", example = "1234567890", required = true) id: String
  ): Action[AnyContent]

  @ApiOperation(
    value = "Ok if edit is made",
    notes = "Invokes a method in sbr-hbase-connector to edit an Enterprise",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "POST"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, responseContainer = "JsValue", message = "Edit has been made successfully to Enterprise with id: [id]"),
    new ApiResponse(code = 400, responseContainer = "JsValue", message = "BadRequest -> id or edit json or period is invalid"),
    new ApiResponse(code = 500, responseContainer = "JsValue", message = "InternalServerError -> Unable to make edit")
  ))
  def editEnterpriseForPeriod(
    @ApiParam(value = "A period in yyyyMM format", example = "201706", required = true) period: String,
    @ApiParam(value = "An Enterprise ID", example = "1234567890", required = true) id: String
  ): Action[AnyContent]

}
