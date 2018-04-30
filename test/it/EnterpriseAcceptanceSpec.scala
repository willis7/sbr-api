import java.time.Month.MARCH

import fixture.ReadsUnitDescriptor.unitDescriptor
import fixture.ServerAcceptanceSpec
import org.scalatest.OptionValues
import play.api.http.HeaderNames.CONTENT_TYPE
import play.api.http.Status.OK
import play.api.libs.json.{ JsValue, Json }
import play.mvc.Http.MimeTypes.JSON
import support.WithWireMockSbrControlApi
import uk.gov.ons.sbr.models.{ Ern, Period, UnitDescriptor }

import scala.util.parsing.json.JSONObject

class EnterpriseAcceptanceSpec extends ServerAcceptanceSpec with WithWireMockSbrControlApi with OptionValues {
  private val TargetErn = Ern("1000000012")
  private val TargetPeriod = Period.fromYearMonth(2018, MARCH)

  private val EnterpriseUnitLinksResponseBody =
    s"""
       |{"id":"${TargetErn.value}",
       | "children":{"10205415":"LEU","900000011":"LOU"},
       | "unitType":"ENT",
       | "period":"201803"
       |}""".stripMargin

  private val EnterpriseUnitResponseBody =
    s"""|{
        | "ern":"${TargetErn.value}",
        | "entref":"some-entref",
        | "name":"some-name",
        | "postcode":"some-postcode",
        | "legalStatus":"some-legalStatus",
        | "employees":42
        |}""".stripMargin

  info("As a SBR user")
  info("I want to retrieve an enterprise for a period in time")
  info("So that I can view the enterprise details via the user interface")

  feature("retrieve an existing Enterprise") {
    scenario("by Enterprise reference (ERN) for a specific period") { wsClient =>
      Given(s"an enterprise unit link exists for an enterprise with $TargetErn for $TargetPeriod")
      stubSbrControlApiFor(anEnterpriseUnitLinksRequest(withErn = TargetErn, withPeriod = TargetPeriod).willReturn(
        anOkResponse().withBody(EnterpriseUnitLinksResponseBody)
      ))
      And(s"an enterprise exists with $TargetErn for $TargetPeriod")
      stubSbrControlApiFor(anEnterpriseForPeriodRequest(withErn = TargetErn, withPeriod = TargetPeriod).willReturn(
        anOkResponse().withBody(EnterpriseUnitResponseBody)
      ))

      When(s"the enterprise with reference $TargetErn is requested for $TargetPeriod")
      val response = await(wsClient.url(s"/v1/periods/${Period.asString(TargetPeriod)}/ents/${TargetErn.value}").get())

      Then(s"the details of the enterprise with $TargetErn for $TargetPeriod are returned")
      response.status shouldBe OK
      response.header(CONTENT_TYPE).value shouldBe JSON

      response.json.as[UnitDescriptor] shouldBe UnitDescriptor(
        id = TargetErn.value,
        children = Map("10205415" -> "LEU", "900000011" -> "LOU"),
        unitType = "ENT",
        period = Period.asString(TargetPeriod),
        vars = Json.parse(EnterpriseUnitResponseBody)
      )
    }
  }
}
