package services

import uk.gov.ons.sbr.models.{ Ern, Period, UnitDescriptor }

import scala.concurrent.Future

trait EnterpriseService {
  def retrieve(period: Period, ern: Ern): Future[Option[UnitDescriptor]]
}
