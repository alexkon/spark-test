package com.example.test

import net.liftweb.json.DefaultFormats
import net.liftweb.json._

object CoordinatesToPolygon {

//  case class Point(lat: Double, lng: Double)
//case class Point(latitude: Double, longitude: Double)
  case class Point(latitude: Double, longitude: Double)
  case class Viewport(northeast: Point, southwest: Point)
  case class PointJS(lat: Double, lng: Double)
  case class ViewportJS(northeast: PointJS, southwest: PointJS)
  implicit def viewportJsToViewPort(viewportJS: ViewportJS):Viewport = {
    Viewport(
      Point(latitude = viewportJS.northeast.lat, longitude = viewportJS.northeast.lng),
      Point(latitude = viewportJS.southwest.lat, longitude = viewportJS.southwest.lng))
  }

  def polygon(northeast: Point, southwest: Point):String = {
    s"POLYGON((${southwest.longitude} ${northeast.latitude},${northeast.longitude} ${northeast.latitude},${northeast.longitude} ${southwest.latitude},${southwest.longitude} ${southwest.latitude}))"
  }

  def parseViewportString(jsonString:String):Viewport = {
    implicit val formats: DefaultFormats.type = DefaultFormats

    val jValue = parse(jsonString)
    if (jsonString.contains("latitude")) jValue.extract[Viewport] else jValue.extract[ViewportJS]
  }

  def viewportStringToPolygon(jsonString:String): String = {
    val viewport = parseViewportString(jsonString)
    polygon(viewport.northeast, viewport.southwest)
  }

  def main(args: Array[String]): Unit = {

    val viewportString =
      """
        |{
        |               "northeast" : {
        |                  "lat" : 14.6493399802915,
        |                  "lng" : 121.0473599802915
        |               },
        |               "southwest" : {
        |                  "lat" : 14.6466420197085,
        |                  "lng" : 121.0446620197085
        |               }
        |            }
        |""".stripMargin

    println(viewportStringToPolygon(viewportString))

  }
}