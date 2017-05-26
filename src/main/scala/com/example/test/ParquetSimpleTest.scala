package com.example.test

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.slf4j.LoggerFactory

/**
  * Created by alexander.konovalov on 26.05.17.
  */
object ParquetSimpleTest extends App {

  private val logger = LoggerFactory.getLogger(getClass)

  val spark = SparkSession
    .builder()
    .appName("Spark Test parquet writing")
    .master("local[1]")
    .getOrCreate()

  val peopleDF = spark.read.json("data/people.json")
  peopleDF.show()

  logger.info("Start writing...")
  peopleDF.write.mode(SaveMode.Overwrite).parquet("data/people.parquet")
  logger.info("Finished")

  spark.close()
}