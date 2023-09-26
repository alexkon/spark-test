package com.example.test

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{IntegerType, LongType}
import org.slf4j.LoggerFactory

object ReadAsiaZipCodes {

  private val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .appName("Spark Test parquet writing")
      .master("local[1]")
      .getOrCreate()

    // Asia zip codes
    val zipCodes = spark.read.option("header", "true").csv("data/zip_to_lat_lon_asia.csv")
    val phZipCodesNumber = zipCodes.filter("country = 'Philippines'").count
    val phUniqueZipCodesNumber = zipCodes.filter("country = 'Philippines'").select("postal_code").distinct.count

    println()
    println(s"phZipCodesNumber = ${phZipCodesNumber}")
    println(s"phUniqueZipCodesNumber = ${phUniqueZipCodesNumber}")


    // Wiki PH zip codes
    val zipCodesWiki = spark.read.option("header", "true").csv("data/ph_zip_codes_wiki.csv")
    val phZipCodesWikiNumber = zipCodesWiki.count
    val phUniqueZipCodesWikiNumber = zipCodesWiki.select("zip_code").distinct.count

    println()
    println(s"phZipCodesWikiNumber = ${phZipCodesWikiNumber}")
    println(s"phUniqueZipCodesWikiNumber = ${phUniqueZipCodesWikiNumber}")

    // geonames PH zip codes
    val zipCodesGeoNames = spark.read.option("delimiter", "\t").option("header", "true").csv("data/geonames_zip_ph.txt")
    val phZipCodesGeoNamesNumber = zipCodesGeoNames.count
    val phUniqueZipCodesGeoNamesNumber = zipCodesGeoNames.select("postal_code").distinct.count

    println()
    println(s"phZipCodesGeoNamesNumber = ${phZipCodesGeoNamesNumber}")
    println(s"phUniqueZipCodesGeoNamesNumber = ${phUniqueZipCodesGeoNamesNumber}")



    // trying to join data
    val wikiUniqueZip = zipCodesWiki.select("zip_code").distinct
    val geoNamesUniqueZip = zipCodesGeoNames.select("postal_code").withColumnRenamed("postal_code", "zip_code").distinct

    val unionZipCodes = geoNamesUniqueZip
      .join(wikiUniqueZip, Seq("zip_code"), "fullouter")
      .withColumn("zip_code", col("zip_code").cast(IntegerType))
      .orderBy("zip_code")
      .distinct

    val innerJoinCount = unionZipCodes.count
    println(s"innerJoinCount = ${innerJoinCount}")

    unionZipCodes.coalesce(1).write.option("header","true").csv("/Users/alexkon/Developer/scala/examples/spark-test/data/ph_zip_codes")
  }
}
