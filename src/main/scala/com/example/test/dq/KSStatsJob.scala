package com.example.test.dq

import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.DoubleType

// spark-submit --class com.onefactor.dq.KSStatsJob dq-ks-stats-assembly-1.0.jar alltran_count_in_out_alltime_alldays_d28 /Users/alexander.konovalov/Developer/scala/education/spark-test/data/all_network_counters_daily_d28/dp=megafon/year=2019/month=01/day=29 /Users/alexander.konovalov/Developer/scala/education/spark-test/data/all_network_counters_daily_d28/dp=megafon/year=2019/month=01/day=30 /Users/alexander.konovalov/Developer/scala/education/spark-test/data/all_network_counters_daily_d28/dp=megafon/year=2019/month=01/day=30/_ks_test_result_d1

object KSStatsJob {

  val relative_error = 0.0001
  val probabilities: Array[Double] = (0 to 100).map(_.toDouble / 100).toArray

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

    val quantiles = df2.stat.approxQuantile(colName, probabilities , relative_error)

    println("Approx quantile:")
    println(s"quantiles = ${quantiles.toSeq}")
    println(s"probabilities = ${probabilities.toSeq}")

    // perform a KS test using a cumulative distribution function of our making
    import spark.implicits._
    val inputRdd = df1
      .select(colName)
      .withColumn(colName, df1(colName).cast(DoubleType))
      .map(_.getAs[Double](0))
      .rdd
    val testResult = Statistics.kolmogorovSmirnovTest(inputRdd, cdfByQuantiles(probabilities, quantiles)(_))
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

  def cdfByQuantiles(probabilities: Array[Double], quantiles: Array[Double])(value: Double): Double = {
    if (quantiles.contains(value)) {
      val index = quantiles.lastIndexOf(value)
      probabilities(index)
    } else {
      val rightIndex = quantiles.indexWhere(value < _)
      if (rightIndex == -1) // return prob for max quantile
        probabilities.last
      else if (rightIndex == 0) // return prob for min quantile
        probabilities.head
      else {
        val leftIndex = rightIndex - 1
        val weight = (value - quantiles(leftIndex)) / (quantiles(rightIndex) - quantiles(leftIndex))
        (probabilities(rightIndex) - probabilities(leftIndex)) * weight + probabilities(leftIndex)
      }
    }
  }
}
case class KSResult(colName: String, pValue: Double, statistic: Double, nullHypothesis: String)
