package com.example.test

import com.koddi.geocoder.{CountryComponent, Geocoder, PostalCodeComponent}
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

object GeocodingTest {
  def main(args: Array[String]): Unit = {

    val MY_KEY = "AIzaSyAR1QiLjtqZBUp7OqlXpA9rnxtMeiqD2xo"
    val geo: Geocoder = Geocoder.create(MY_KEY)

    val postalCode = "1105"
    val unstructuredResults = geo.lookup(s"Philippines ${postalCode}")
    val structuredResults = geo.lookup(Seq(CountryComponent("PH"), PostalCodeComponent(postalCode)))

    implicit val formats = DefaultFormats
    val jsonStringUnstructured = write(unstructuredResults)
    val jsonStringStructured = write(structuredResults)

    println("Unstructured result:")
    println(jsonStringUnstructured)
    println()

    println("Structured result:")
    println(jsonStringStructured)
  }


}
