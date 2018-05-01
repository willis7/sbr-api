package repository.sbrctrl

import repository.UnitLinksRepository
import uk.gov.ons.sbr.models.{ Ern, Period, UnitLinks }

import scala.concurrent.Future

class SbrCtrlUnitLinksRepository extends UnitLinksRepository {
  override def retrieveEnterpriseLinks(period: Period, ern: Ern): Future[Option[UnitLinks]] = ???
}
