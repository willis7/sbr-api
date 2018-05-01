package services.sbrctrl

import javax.inject.Inject
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import repository.{ EnterpriseRepository, UnitLinksRepository }
import services.EnterpriseService
import uk.gov.ons.sbr.models.{ Ern, Period, UnitDescriptor, UnitLinks }

import scala.concurrent.Future

class SbrCtrlEnterpriseService @Inject() (unitLinksRepository: UnitLinksRepository, enterpriseRepository: EnterpriseRepository) extends EnterpriseService {
  override def retrieve(period: Period, ern: Ern): Future[Option[UnitDescriptor]] =
    unitLinksRepository.retrieveEnterpriseLinks(period, ern).flatMap {
      _.fold(onUnitLinksNotFound) { unitLinks =>
        onUnitLinksFound(period, ern, unitLinks)
      }
    }

  private def onUnitLinksNotFound: Future[Option[UnitDescriptor]] =
    Future.successful(None)

  private def onUnitLinksFound(period: Period, ern: Ern, unitLinks: UnitLinks): Future[Option[UnitDescriptor]] =
    enterpriseRepository.retrieveEnterprise(period, ern).map { optJson =>
      optJson.map {
        UnitDescriptor.wrap(unitLinks, _)
      }
    }
}
