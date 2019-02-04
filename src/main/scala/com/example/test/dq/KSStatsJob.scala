package com.example.test.dq

import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.sql.SparkSession

// spark-submit --class com.onefactor.dq.KSStatsJob dq-ks-stats-assembly-1.0.jar alltran_count_in_out_alltime_alldays_d28 /Users/alexander.konovalov/Developer/scala/education/spark-test/data/all_network_counters_daily_d28/dp=megafon/year=2019/month=01/day=29 /Users/alexander.konovalov/Developer/scala/education/spark-test/data/all_network_counters_daily_d28/dp=megafon/year=2019/month=01/day=30 /Users/alexander.konovalov/Developer/scala/education/spark-test/data/all_network_counters_daily_d28/dp=megafon/year=2019/month=01/day=30/_ks_test_result_d1

object KSStatsJob {

  val relative_error = 0.0001
  val probabilities = (1 until 10).map(_.toDouble / 10).toArray

  def main(args: Array[String]): Unit = {

    // spark (local test)
    val spark = SparkSession.builder().master("local[*]").appName("myApp").config("spark.sql.parquet.compression.codec", "gzip").getOrCreate()

    // input variables (local test)
    val colName = "alltran_count_in_out_alltime_alldays_d28"
    val pathPrev = "data/all_network_counters_daily_d28/dp=megafon/year=2019/month=01/day=29"
    val pathCurr = "data/all_network_counters_daily_d28/dp=megafon/year=2019/month=01/day=30"
    val outputPath = "data/all_network_counters_daily_d28/dp=megafon/year=2019/month=01/day=30/_ks_test_result_d1"

//    // input variables
//    val colName = args(0)
//    val pathPrev = args(1)
//    val pathCurr = args(2)
//    val outputPath = args(3)


    val df1 = spark.read.parquet(pathPrev)
    val df2 = spark.read.parquet(pathCurr)

    val quantiles1 = df1.stat.approxQuantile(colName, probabilities , relative_error).toSeq
    val quantiles2 = df2.stat.approxQuantile(colName, probabilities , relative_error).toSeq

    println("Approx quantile:")
    println(s"quantiles1 = $quantiles1")
    println(s"quantiles2 = $quantiles2")

    // perform a KS test using a cumulative distribution function of our making
    val myCDF = quantiles1.zip(quantiles2).toMap
    println(s"myCDF = ${myCDF}")
    val testResult = Statistics.kolmogorovSmirnovTest(spark.sparkContext.parallelize(quantiles1), myCDF)
    println(testResult)

    // write result
    import spark.implicits._
    val resDF = Seq(KSResult(colName, testResult.pValue, testResult.statistic, testResult.nullHypothesis)).toDS()

    resDF
      .write
      .option("header", "true")
      .option("delimiter", "\t")
      .csv(outputPath)

    resDF.show(false)
  }
}
case class KSResult(colName: String, pValue: Double, statistic: Double, nullHypothesis: String)
