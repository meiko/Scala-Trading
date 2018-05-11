package uk.co.tradingdevelopment.trading.scala.domain

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONHandler, Macros}
import uk.co.tradingdevelopment.trading.scala.objects.{ContractType, TradingEnvironment}
import uk.co.tradingdevelopment.trading.scala.objects.TradingEnvironment._
import uk.co.tradingdevelopment.trading.scala.objects.ContractType._
case class Account(_id:String,
                   alias: String,
                   ProviderName: String,
                   authDetails: Map[String, String],
                   isForPricing: Boolean,
                   environment: TradingEnvironment,
                   contractType: ContractType)


object Account{
  implicit val accountHandler: BSONHandler[BSONDocument, Account] =
    Macros.handler[Account]

  implicit val tradingEnvironmentHandler: BSONHandler[BSONDocument, TradingEnvironment] =
    Macros.handler[TradingEnvironment]
  implicit val tradingContractType: BSONHandler[BSONDocument, ContractType] =
    Macros.handler[ContractType]
}