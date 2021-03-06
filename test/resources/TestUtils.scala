package resources

import org.scalatest.FlatSpec
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import play.api.libs.json._
import play.api.test.FakeRequest
import play.api.test.Helpers._

/**
 * Created by Ameen on 15/07/2017.
 */

trait TestUtils extends PlaySpec with GuiceOneAppPerSuite {

  protected[this] def fakeRequest(url: String, method: String = GET) =
    route(app, FakeRequest(method, url)).getOrElse(sys.error(s"Route $url does not exist"))

  protected def requestObject(url: String, method: String = GET) =
    FakeRequest(GET, "/").withJsonBody(Json.parse("""{ "field": "value" }"""))

  protected def getValue(json: Option[String]): String = json match {
    case Some(x: String) => s"$x"
    case _ => sys.error("No Value failed. Forcing test failure")
  }

  protected def getJsValue(elem: JsLookupResult) = elem match {
    case JsDefined(y) => s"$y"
    case _ => sys.error("No JsValue found. Forcing test failure")
  }

  protected def instanceName(s: String, regex: String = "."): String = s.substring(s.lastIndexOf(regex) + 1)

}
