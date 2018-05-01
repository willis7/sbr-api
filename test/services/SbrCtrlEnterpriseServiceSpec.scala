package services

import java.time.Month.FEBRUARY

import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ FreeSpec, Matchers, OptionValues }
import repository.{ EnterpriseRepository, UnitLinksRepository }
import services.sbrctrl.SbrCtrlEnterpriseService
import support.sample.SampleEnterprise
import uk.gov.ons.sbr.models.UnitType.{ Enterprise, LocalUnit }
import uk.gov.ons.sbr.models._

import scala.concurrent.Future

class SbrCtrlEnterpriseServiceSpec extends FreeSpec with Matchers with MockFactory with ScalaFutures with OptionValues {

  private trait Fixture {
    val TargetErn = Ern("1234567890")
    val TargetPeriod = Period.fromYearMonth(2018, FEBRUARY)
    val EnterpriseJson = SampleEnterprise.asJson(TargetErn)

    val unitLinksRepository = mock[UnitLinksRepository]
    val enterpriseRepository = mock[EnterpriseRepository]
    val service = new SbrCtrlEnterpriseService(unitLinksRepository, enterpriseRepository)

    def enterpriseUnitLinks(withPeriod: Period, withErn: Ern, withChildren: Seq[IdentifierAndType]): UnitLinks =
      UnitLinks(
        IdentifierAndType(withErn.value, Enterprise),
        withPeriod,
        withChildren
      )
  }

  "An Enterprise Service" - {
    "assembles an enterprise with its associated links" - {
      "when both the unit link and enterprise entries are found for the target Enterprise reference (ERN) and period" in new Fixture {
        val enterpriseChildLinks = Seq(IdentifierAndType("123456789", LocalUnit))
        (unitLinksRepository.retrieveEnterpriseLinks _).expects(TargetPeriod, TargetErn).returning(Future.successful(
          Some(enterpriseUnitLinks(withPeriod = TargetPeriod, withErn = TargetErn, withChildren = enterpriseChildLinks))
        ))
        (enterpriseRepository.retrieveEnterprise _).expects(TargetPeriod, TargetErn).returning(Future.successful(
          Some(EnterpriseJson)
        ))

        whenReady(service.retrieve(TargetPeriod, TargetErn)) { result =>
          result.value shouldBe UnitDescriptor(
            idAndType = IdentifierAndType(TargetErn.value, Enterprise),
            period = TargetPeriod,
            children = enterpriseChildLinks,
            unit = EnterpriseJson
          )
        }
      }
    }
  }
}
