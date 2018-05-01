package controllers.v1

import java.time.Month.FEBRUARY

import org.scalamock.scalatest.MockFactory
import org.scalatest.{ FreeSpec, Matchers, OptionValues }
import play.api.libs.json._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.EnterpriseService
import support.sample.SampleEnterprise
import uk.gov.ons.sbr.models.UnitType.{ Enterprise, LocalUnit }
import uk.gov.ons.sbr.models._

import scala.concurrent.Future

class EnterpriseControllerSpec extends FreeSpec with Matchers with MockFactory with OptionValues {

  private trait Fixture {
    val TargetErn = Ern("1234567890")
    val TargetPeriod = Period.fromYearMonth(2018, FEBRUARY)
    val EnterpriseDescriptor = enterpriseDescriptorFor(TargetPeriod, TargetErn)
    val EnterpriseJsonRepresentation = JsObject(Seq("foo" -> JsString("bar")))

    val enterpriseService = mock[EnterpriseService]
    val writesUnitDescriptor = mock[Writes[UnitDescriptor]]
    val controller = new EnterpriseController(enterpriseService, writesUnitDescriptor)

    private def enterpriseDescriptorFor(period: Period, ern: Ern): UnitDescriptor =
      UnitDescriptor(
        idAndType = IdentifierAndType(ern.value, Enterprise),
        period,
        children = Seq(IdentifierAndType("987654321", LocalUnit)),
        unit = SampleEnterprise.asJson(TargetErn)
      )
  }

  "A request" - {
    "to retrieve an Enterprise for a period by the Enterprise reference (ERN)" - {
      "returns a JSON representation of the enterprise and its relations when it is found" in new Fixture {
        (enterpriseService.retrieve _).expects(TargetPeriod, TargetErn).returning(Future.successful(
          Some(EnterpriseDescriptor)
        ))
        (writesUnitDescriptor.writes _).expects(EnterpriseDescriptor).returning(EnterpriseJsonRepresentation)

        val action = controller.retrieveEnterprise(Period.asString(TargetPeriod), TargetErn.value)
        val response = action.apply(FakeRequest())

        status(response) shouldBe OK
        contentType(response).value shouldBe JSON
        contentAsJson(response) shouldBe EnterpriseJsonRepresentation
      }
    }
  }
}
