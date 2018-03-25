import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

//case class Location(lat: Double, long: Double)
//case class Resident(name: String, age: Int, role: Option[String])
case class Place(name: String, location: String)



//implicit val locationReads: Reads[Location] = (
//  (JsPath \ "lat").read[Double](min(-90.0) keepAnd max(90.0)) and
//    (JsPath \ "long").read[Double](min(-180.0) keepAnd max(180.0))
//  )(Location.apply _)
//
//implicit val residentReads: Reads[Resident] = (
//  (JsPath \ "name").read[String](minLength[String](2)) and
//    (JsPath \ "age").read[Int](min(0) keepAnd max(150)) and
//    (JsPath \ "role").readNullable[String]
//  )(Resident.apply _)

object Place {
  implicit val placeReads: Reads[Place] = (
    (JsPath \ "name").read[String](minLength[String](2)) and
      (JsPath \ "location" \ "lat").read[String]
    ) (Place.apply _)


  val json: JsValue = Json.parse(
    """
{
  "name" : "Watership Down",
  "location" : {
    "lat" : 51.235685,
    "long" : -1.309197
  },
  "residents" : [ {
    "name" : "Fiver",
    "age" : 4,
    "role" : null
  }, {
    "name" : "Bigwig",
    "age" : 6,
    "role" : "Owsla"
  } ]
}
""")

  val h = json.validate[Place] match {
    case s: JsSuccess[Place] => {
      val place: Place = s.get
      println(place)
      // do something with place
    }
    case e: JsError => {
      println(e)
      // error handling flow
    }
  }
  println(h)
}

trait SM
case class d(s: String)  extends SM


def m(r: SM) = {
  r.equals(d("p"))
}

m(d("p"))