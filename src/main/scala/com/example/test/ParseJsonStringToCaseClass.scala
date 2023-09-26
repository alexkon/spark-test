package com.example.test

import com.example.test.CoordinatesToPolygon.{Viewport, ViewportJS}
import net.liftweb.json.parse
import net.liftweb.json.DefaultFormats
import net.liftweb.json._

object ParseJsonStringToCaseClass {

  def main(args: Array[String]): Unit = {
    implicit val formats: DefaultFormats.type = DefaultFormats

    val jValue = parse(jsonString)
    val request = jValue.extract[TransactionEvaluationRequest]
    println(s"request = ${request}")
  }

  def jsonString: String = {
    """
      |{
      |    "request_id_z": "4fwks3bnt45467bp23foih345345glj632",
      |    "request_timestamp": "2018-10-09T08:19:16.999578+02:00",
      |    "evaluation_entity": "transaction",
      |    "attributes": {
      |        "session": {
      |            "ip": ["192.168.0.1"],
      |            "timestamp": "2010-01-01T12:00:00+0100",
      |            "ip_geolocation_cloudflare": "PH",
      |            "user_agent": "Mozilla/5.0 (Linux; Android 7.0) AppleWebKit/537.36 Mobile Safari/537.36",
      |            "user_session_id": "h2387dgsdf8gh24tk23g3",
      |            "user_id": "100e81f1eb814cb0bc52cd820e458447"
      |        },
      |        "transaction": {
      |            "id": "30bb842479574fcfbe6bcd3708771579",
      |            "entry_type": "outgoing",
      |            "is_internal": false,
      |            "is_exchange": false,
      |            "currency": "PBTC",
      |            "amount": "50.0145560000000000",
      |            "fee_amount": "0.000000000000000000",
      |            "message": "-",
      |            "reference": {
      |                "reason_code": "sell_order",
      |                "order_id": "6789fds9sdf23g3f834f34f"
      |            },
      |            "priority": "low",
      |            "running_balance": "0.020038370000000000"
      |        },
      |        "sender": {
      |            "user_id": "816606ec49574dbf82323938fc0715f3",
      |            "account_id": "32db75c6891447799f06e252fa9a13a8"
      |        },
      |        "receiver": {
      |            "original_target_name": "",
      |            "original_target_address": "fake@yahoo.com",
      |            "original_target_address_type": "email",
      |            "original_target_user_id": "d568a55c6c894ec4ac5b0052ecd4b430"
      |        }
      |    }
      |}
      |""".stripMargin
  }
}
