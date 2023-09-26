package com.example.test

import com.koddi.geocoder.{CountryComponent, Geocoder, PostalCodeComponent}
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

object GeocodingTestBatch {
  def main(args: Array[String]): Unit = {

    val MY_KEY = sys.env.getOrElse("GOOGLE_API_KEY", throw new RuntimeException("Please provide Google API Key"))
    val geo: Geocoder = Geocoder.create(MY_KEY)
    implicit val formats: DefaultFormats.type = DefaultFormats

    val postalCodes = Seq("1005", "1102", "1104", "1105", "1106", "1107")
//    val postalCodes = Seq("1105")

    println("Unstructured result:")
    postalCodes.foreach(postalCode => {
      val result = geo.lookup(s"Philippines ${postalCode}")
      Thread.sleep(1000)
      println(s"$postalCode: ${write(result)}")
    })

    println("\nStructured result:")
    postalCodes.foreach(postalCode => {
      val result = geo.lookup(Seq(CountryComponent("PH"), PostalCodeComponent(postalCode)))
      Thread.sleep(1000)
      println(s"$postalCode: ${write(result)}")
    })
  }
}
