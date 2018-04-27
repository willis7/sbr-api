package fixture

import play.api.libs.json.Json
import uk.gov.ons.sbr.models.UnitDescriptor

object ReadsUnitDescriptor {
  implicit val unitDescriptor = Json.reads[UnitDescriptor]
}
