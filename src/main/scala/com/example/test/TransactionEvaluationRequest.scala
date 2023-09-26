package com.example.test

final case class TransactionEvaluationRequest(
  request_id: String,
  request_timestamp: String,
  evaluation_entity: String,
  attributes: TransactionEvaluationAttributesRequest
) extends EvaluationRequest

final case class TransactionEvaluationAttributesRequest(
  session: EvaluationSessionRequest,
  channel: Option[TransactionEvaluationAttributesChannelRequest],
  transaction: TransactionEvaluationAttributesTransactionRequest,
  sender: TransactionEvaluationAttributesSenderRequest,
  receiver: TransactionEvaluationAttributesReceiverRequest
) extends EvaluationAttributesRequest

final case class TransactionEvaluationAttributesChannelRequest(
  partner: String
)

final case class TransactionEvaluationAttributesTransactionRequest(
  id: String,
  entry_type: String,
  is_internal: Boolean,
  is_exchange: Boolean,
  currency: String,
  amount: String,
  fee_amount: String,
  message: String,
  reference: TransactionEvaluationAttributesTransactionReferenceRequest,
  priority: String,
  running_balance: String
)

final case class TransactionEvaluationAttributesTransactionReferenceRequest(
  reason_code: String,
  order_id: String
)

final case class TransactionEvaluationAttributesSenderRequest(
  user_id: String,
  account_id: String
)

final case class TransactionEvaluationAttributesReceiverRequest(
  original_target_name: String,
  original_target_address: String,
  original_target_address_type: String,
  original_target_user_id: String
)

trait EvaluationRequest {
  val request_id: String
  val request_timestamp: String
  val evaluation_entity: String
  val attributes: EvaluationAttributesRequest
}

trait EvaluationAttributesRequest {
  val session: EvaluationSessionRequest
}

final case class EvaluationSessionRequest(
                                           ip: List[String],
                                           timestamp: String,
                                           ip_geolocation_cloudflare: String,
                                           user_agent: String,
                                           user_session_id: String,
                                           user_id: String,
                                           device_id: Option[String]
                                         )
