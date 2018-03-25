package swagger

import play.api.mvc.{ Action, AnyContent }
import io.swagger.annotations._

/**
 * SearchControllerSwagger
 * ----------------
 * Author: haqa
 * Date: 10 July 2017 - 09:25
 * Copyright (c) 2017  Office for National Statistics
 */

trait SearchControllerSwagger {

  //public api
  @ApiOperation(
    value = "Json id match or a list of unit conflicts",
    notes = "The matches can occur from any id field and multiple records can be matched",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, responseContainer = "JSONObject", message = "Success -> Record(s) found for id."),
    new ApiResponse(code = 400, responseContainer = "JSONObject", message = "Client Side Error -> Required " +
      "parameter was not found."),
    new ApiResponse(code = 404, responseContainer = "JSONObject", message = "Client Side Error -> Id not found."),
    new ApiResponse(code = 500, responseContainer = "JSONObject", message = "Server Side Error -> Request " +
      "could not be completed.")
  ))
  def searchById(
    @ApiParam(value = "An identifier of any type", example = "825039145000", required = true) id: Option[String],
    @ApiParam(value = "A numerical limit", example = "6", required = false) history: Option[Int]
  ): Action[AnyContent]

  //public api
  @ApiOperation(
    value = "Json id and period match or a list of unit conflicts",
    notes = "The matches can occur from any id field and multiple records can be matched",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, responseContainer = "JSONObject", message = "Success -> Record(s) found for id."),
    new ApiResponse(code = 400, responseContainer = "JSONObject", message = "Client Side Error -> Required " +
      "parameter was not found."),
    new ApiResponse(code = 404, responseContainer = "JSONObject", message = "Client Side Error -> Id not found."),
    new ApiResponse(code = 500, responseContainer = "JSONObject", message = "Server Side Error -> " +
      "Request could not be completed.")
  ))
  def searchByPeriod(
    @ApiParam(value = "An identifier of any type", example = "825039145000", required = true) id: Option[String],
    @ApiParam(value = "Identifier creation date", example = "2017/07", required = true) period: String
  ): Action[AnyContent]

  @ApiOperation(
    value = "Json Object of matching legal unit",
    notes = "Sends request to sub api of admin data",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success - Displays an exact LEU record matching id and period."),
    new ApiResponse(code = 500, responseContainer = "Json", message = "Internal Server Error - Request timed-out."),
    new ApiResponse(code = 500, responseContainer = "Json", message = "Internal Server Error - " +
      "Failed to connection or timeout with endpoint.")
  ))
  def searchLeu(
    @ApiParam(value = "Identifier creation date", example = "2017/07", required = true) period: String,
    @ApiParam(value = "A legal unit identifier", example = "<some example>", required = true) id: String
  ): Action[AnyContent]

  @ApiOperation(
    value = "Json Object of matching enterprise",
    notes = "Sends request to control api to get enterprise",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success - Displays an exact enterprise record matching id and period."),
    new ApiResponse(code = 500, responseContainer = "Json", message = "Internal Server Error - Request timed-out."),
    new ApiResponse(code = 500, responseContainer = "Json", message = "Internal Server Error - " +
      "Failed to connection or timeout with endpoint.")
  ))
  def searchEnterprise(
    @ApiParam(value = "Identifier creation period", example = "2017/07", required = true) period: String,
    @ApiParam(value = "A legal unit identifier", example = "<some example>", required = true) id: String
  ): Action[AnyContent]

  @ApiOperation(
    value = "Json Object of matching vat",
    notes = "Sends request to sub api of admin data",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success - Displays an exact vat record matching id and period."),
    new ApiResponse(code = 500, responseContainer = "Json", message = "Internal Server Error - Request timed-out."),
    new ApiResponse(code = 500, responseContainer = "Json", message = "Internal Server Error - " +
      "Failed to connection or timeout with endpoint.")
  ))
  def searchVat(
    @ApiParam(value = "Identifier creation period", example = "2017/07", required = true) period: String,
    @ApiParam(value = "A legal unit identifier", example = "<some example>", required = true) id: String
  ): Action[AnyContent]

  @ApiOperation(
    value = "Json Object of matching paye",
    notes = "Sends request to sub api of admin data",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success - Displays an exact paye record matching id and period."),
    new ApiResponse(code = 500, responseContainer = "Json", message = "Internal Server Error - Request timed-out."),
    new ApiResponse(code = 500, responseContainer = "Json", message = "Internal Server Error - " +
      "Failed to connection or timeout with endpoint.")
  ))
  def searchPaye(
    @ApiParam(value = "Identifier creation period", example = "2017/07", required = true) period: String,
    @ApiParam(value = "A legal unit identifier", example = "<some example>", required = true) id: String
  ): Action[AnyContent]

  @ApiOperation(
    value = "Json Object of matching crn",
    notes = "Sends request to sub api of admin data",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success - Displays an exact crn record matching id and period"),
    new ApiResponse(code = 500, responseContainer = "Json", message = "Internal Server Error - Request timed-out."),
    new ApiResponse(code = 500, responseContainer = "Json", message = "Internal Server Error - " +
      "Failed to connection or timeout with endpoint.")
  ))
  def searchCrn(
    @ApiParam(value = "Identifier creation period", example = "2017/07", required = true) period: String,
    @ApiParam(value = "A companies reference number identifier", example = "<some example>", required = true) id: String
  ): Action[AnyContent]

}
