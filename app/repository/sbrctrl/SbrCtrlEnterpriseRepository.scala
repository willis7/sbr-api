package repository.sbrctrl

import play.api.libs.json.JsObject
import repository.EnterpriseRepository
import uk.gov.ons.sbr.models.{ Ern, Period }

import scala.concurrent.Future

class SbrCtrlEnterpriseRepository extends EnterpriseRepository {
  override def retrieveEnterprise(period: Period, ern: Ern): Future[Option[JsObject]] = ???
}
