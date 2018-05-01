package repository

import uk.gov.ons.sbr.models.{ Ern, Period, UnitLinks }

import scala.concurrent.Future

trait UnitLinksRepository {
  def retrieveEnterpriseLinks(period: Period, ern: Ern): Future[Option[UnitLinks]]
}
