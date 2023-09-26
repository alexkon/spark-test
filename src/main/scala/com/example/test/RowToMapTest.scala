package com.example.test

import org.apache.spark.sql.catalyst.expressions.objects.AssertNotNull
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{MapType, StringType, StructField, StructType}
import org.apache.spark.sql.{Column, Row, SparkSession}
import org.slf4j.LoggerFactory

/**
  * Created by alexander.konovalov on 26.05.17.
  */
object RowToMapTest extends App {


  private val logger = LoggerFactory.getLogger(getClass)

  val spark = SparkSession
    .builder()
    .appName("Spark Test parquet writing")
    .master("local[1]")
    .getOrCreate()

  val peopleDF = spark.read.json("data/people.json")
  peopleDF.show()

  val inputStringDF = peopleDF.columns.foldLeft(peopleDF){case (df, c) => df.withColumn(c, col(c).cast(StringType))}
  val rdd = inputStringDF.rdd.map(row => Row(row.getValuesMap[String](row.schema.fieldNames)))
  val schema = StructType(Seq(StructField("parameters", MapType(StringType, StringType))))
  val df = spark.createDataFrame(rdd, schema)


  df.show

  import org.apache.spark.sql.functions._
  import org.apache.spark.sql.types._

  val smallSubsetOfNewRecords =
    Seq(
      Row("01EN8C48BHRPF9ZRVM4PJ2V8Q3", "assessment_dashboard_transaction", "g01en8c445sfy8tmt2t23y6wv6g"),
      Row("01EN8C17JTJ26C8SNXRWP0H66D", "assessment_dashboard_transaction", "g01en8c148xavvnmeez2p08dzax")
    )

  val newRecords = smallSubsetOfNewRecords
  val newRecordsSchema = Seq(
    StructField("request_id", StringType, false),
    StructField("request_type", StringType, false),
    StructField("external_id", StringType, true)
  )
  val newRecordsPayload = spark.createDataFrame(
    spark.sparkContext.parallelize(newRecords),
    StructType(newRecordsSchema)
  )

  val paramColumns =
    newRecordsPayload
      .select("external_id")
      .columns
      .flatMap(c => Seq(lit(c), col(c)))


  val requestsWithParams =
    newRecordsPayload
      .withColumn("request_id_non_null", new Column(AssertNotNull(col("request_id").expr)))
      .withColumn("request_type_non_null", new Column(AssertNotNull(col("request_type").expr)))
      .select(
        col("request_id_non_null").as("request_id"),
        col("request_type_non_null").as("request_type"),
        map(paramColumns:_*).alias("params")
      )
}
