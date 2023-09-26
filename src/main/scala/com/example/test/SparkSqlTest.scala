package com.example.test

import org.apache.spark.sql.SparkSession

object SparkSqlTest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Spark Json Test")
      .master("local[1]")
      .getOrCreate()

    val peopleDF = spark.read.json("data/people.json")
    peopleDF.createOrReplaceTempView("people")

    val res = spark.sql(
      """
        |with base as (
        |     select age,
        |            name
        |       from people
        |)
        |
        |SELECT age, count(1) as total from base group by 1 order by 2 desc
        |""".stripMargin)

    res.show
  }
}
