package controllers.v1

import javax.inject.{ Inject, Singleton }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{ Json, Writes }
import play.api.mvc.{ Action, AnyContent, Controller, Result }
import services.EnterpriseService
import uk.gov.ons.sbr.models.{ Ern, Period, UnitDescriptor }

@Singleton
class EnterpriseController @Inject() (enterpriseService: EnterpriseService, writesUnitDescriptor: Writes[UnitDescriptor]) extends Controller {
  def retrieveEnterprise(periodStr: String, ernStr: String): Action[AnyContent] = Action.async {
    enterpriseService.retrieve(Period.fromString(periodStr), Ern(ernStr)).map { optUnitDescriptor =>
      optUnitDescriptor.fold[Result](NotImplemented) { unitDescriptor =>
        Ok(Json.toJson(unitDescriptor)(writesUnitDescriptor))
      }
    }
  }
}
