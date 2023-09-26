package com.example.test

import com.koddi.geocoder.{AdministrativeAreaComponent, CountryComponent, Geocoder, LocalityComponent, PostalCodeComponent}
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

object GeocodingTestBatchEnriched {
  def main(args: Array[String]): Unit = {

    val MY_KEY = sys.env.getOrElse("GOOGLE_API_KEY", throw new RuntimeException("Please provide Google API Key"))
    val geo: Geocoder = Geocoder.create(MY_KEY)
    implicit val formats: DefaultFormats.type = DefaultFormats

    val postalCodes = Seq(
      ("1005", "Manila"),
      ("1102", "Manila"),
      ("1104", "Manila"),
      ("1105", "NCR"),
      ("1106", "Manila"),
      ("1107", "Manila")
    )

    println("Unstructured result:")
    postalCodes.foreach { case (postalCode, city) =>
      val result = geo.lookup(s"Philippines $city $postalCode")
      Thread.sleep(100)
      println(s"$postalCode: ${write(result)}")
    }

    println("\nStructured result:")
    postalCodes.foreach { case (postalCode, city) =>
      println(s"city = ${city}")
      val result = geo.lookup(Seq(CountryComponent("PH"), AdministrativeAreaComponent(city), PostalCodeComponent(postalCode)))
      Thread.sleep(100)
      println(s"$postalCode: ${write(result)}")
    }
  }
}
