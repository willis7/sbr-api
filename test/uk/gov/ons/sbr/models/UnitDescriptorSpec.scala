package uk.gov.ons.sbr.models

import java.time.Month.MARCH

import org.scalatest.{ FreeSpec, Matchers }
import support.sample.SampleEnterprise

class UnitDescriptorSpec extends FreeSpec with Matchers {

  private trait Fixture {
    val SomeErn = Ern("1234567890")
    val SomeEnterpriseIdAndType = IdentifierAndType(SomeErn.value, UnitType.Enterprise)
    val SomePeriod = Period.fromYearMonth(2018, MARCH)
    val SomeEnterpriseChildren = Seq(
      IdentifierAndType("123456789", UnitType.LocalUnit),
      IdentifierAndType("87654321", UnitType.LegalUnit)
    )
    val SomeEnterpriseJson = SampleEnterprise.asJson(SomeErn)
  }

  "A UnitDescriptor" - {
    "can be created by wrapping unit links and a unit" in new Fixture {
      val unitLinks = UnitLinks(
        SomeEnterpriseIdAndType,
        SomePeriod,
        SomeEnterpriseChildren
      )

      UnitDescriptor.wrap(unitLinks, SomeEnterpriseJson) shouldBe UnitDescriptor(
        SomeEnterpriseIdAndType,
        SomePeriod,
        SomeEnterpriseChildren,
        SomeEnterpriseJson
      )
    }
  }
}
