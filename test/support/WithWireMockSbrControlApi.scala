package support

import com.github.tomakehurst.wiremock.client.WireMock.{ aResponse, get, urlEqualTo }
import com.github.tomakehurst.wiremock.client.{ MappingBuilder, ResponseDefinitionBuilder, WireMock }
import org.scalatest.Suite
import play.api.http.Status.OK
import uk.gov.ons.sbr.models.{ Ern, Period }

trait WithWireMockSbrControlApi extends WithWireMock { this: Suite =>
  override val wireMockPort = 9001

  def anEnterpriseUnitLinksRequest(withErn: Ern, withPeriod: Period): MappingBuilder =
    get(urlEqualTo(s"/v1/periods/${Period.asString(withPeriod)}/types/ENT/units/${withErn.value}"))

  def anEnterpriseForPeriodRequest(withErn: Ern, withPeriod: Period): MappingBuilder =
    get(urlEqualTo(s"/v1/periods/${Period.asString(withPeriod)}/enterprises/${withErn.value}"))

  def anOkResponse(): ResponseDefinitionBuilder =
    aResponse().withStatus(OK)

  val stubSbrControlApiFor: MappingBuilder => Unit =
    WireMock.stubFor
}
