package com.example.test

object SqlGeneratorExample {
  def main(args: Array[String]): Unit = {
    
    
    val res = getTransactionStats(transactionType.head, timeWindowTransactions)
    println(s"res = ${res}")

    val resNew = getTransactionStatsNew(transactionType, timeWindowTransactions)
    println(s"resNew = ${resNew}")
  }
  
  val timeWindow = List(24, 48, 168, 336, 99999)
  val timeWindowTransactions = List(336, 720, 1440, 2160, 99999)
  val transactionType = Seq("ftt", "ftd", "ftd_fiat", "ftd_crypto"
  )

    // cycle for ftd/ftt metrics
    def getTransactionStats(date_type: String, timeWindow: List[Int]): String =
      s"""

                     --- ${date_type} ---
                      ${timeWindowTransactions
        .map(x =>
          s"count(if(datediff(${date_type}_date, user_created_at_local) <= ${x / 24}, user_id, null))  as ${date_type}_users_${if (x == timeWindow.last) "all"
          else "d" + x / 24},"
        )
        .mkString("\n")}
                      """

    def getPrefix(date_type: String): String = s"\n                     --- ${date_type} ---"



  def getTransactionStatsNew(date_type: Seq[String], timeWindow: List[Int]): String = {
    date_type
      .map(getTransactionStatsNew(_, timeWindow))
      .mkString("\n")
  }

    def getTransactionStatsNew(date_type: String, timeWindow: List[Int]): String = {
      val metrics = timeWindow
        .map(
          win => {
            val winHours = win / 24
            val userSuffix = if (win == timeWindow.last) "all" else "d" + winHours
            s"count(if(datediff(${date_type}_date, user_created_at_local) <= $winHours, user_id, null)) as ${date_type}_users_$userSuffix,"
          })
      (getPrefix(date_type) :: metrics).mkString("\n")
    }
  
}
