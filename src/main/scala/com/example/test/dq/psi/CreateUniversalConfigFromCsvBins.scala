package com.example.test.dq.psi

import scala.io.Source

object CreateUniversalConfigFromCsvBins {

  def main(args: Array[String]): Unit = {

    val dataDir = "data/psi"
    val binsFileName = "tcs_bins.csv"
    val binsSampleFileName = "tcs_bins_etalon.csv"
    val outputFileName = "features.conf"

    val binsLines = readFileAsLines(s"$dataDir/$binsFileName")
    val binsSampleLines = readFileAsLines(s"$dataDir/$binsSampleFileName")

    val bins: Seq[Bin] = binsLines
      .tail
      .map(line => line.split("\t").toList)
      .map {case List(name, segment, b) => Bin(name, segment, b.filterNot(c => c == '[' || c == ']').split(",").map(_.toDouble).sorted.toList)}

    val binsSample: Map[(String, String), Seq[String]] = binsSampleLines
      .tail
      .map(line => line.split("\t").toList)
      .map {case List(segment, name, group, num) => BinSampleRaw(segment, name, group.toDouble, num.toInt)}
      .foldLeft(Map.empty[(String, String), Seq[BinSampleRaw]])
        { case (m, b) => m + (((b.name, b.segment), b +: m.getOrElse((b.name, b.segment), Seq.empty[BinSampleRaw])))}
      .map { case (k, v) => k -> v.sortBy(_.group).map(_.num) }
      .map { case (k, v) => k -> v.map(_.toDouble / v.sum)}
      .map { case (k, v) => k -> v.map(i => f"$i%.6f").map(_.replace(',', '.'))}

    val universalBins = bins.map(b => UniversalBin(b.name, b.segment, b.bounds, binsSample.getOrElse((b.name, b.segment), Seq.empty[String])))

    println("Output config file content:")
    universalBins.foreach(println)

    new java.io.PrintWriter(s"$dataDir/$outputFileName") { try {write(universalBins.mkString("\n"))} finally {close()}}
  }

  def readFileAsLines(fileName: String): Seq[String] = {
    val s = Source.fromFile(fileName)
    try {s.getLines().toList} finally {s.close()}
  }
}

case class Bin(name: String, segment:String, bounds: Seq[Double])
case class BinSampleRaw(segment:String, name: String, group:Double, num: Int)
case class UniversalBin(name: String, segment:String, bounds: Seq[Double], population: Seq[String]) {

  override def toString: String = {
    val span = "   "
    s"""$name = [
$span{
${span * 2}bounds = [${bounds.mkString(", ")}]
${span * 2}filter = {segment: $segment}
${span * 2}key-columns = [name, segment]
${span * 2}population = [${population.mkString(", ")}]
$span}
]""".stripMargin
  }
}