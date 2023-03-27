package com.example.market

import cats.effect.Sync
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import cats.implicits._
import org.http4s.circe._
import scala.collection.mutable.{Map => Map}

trait StockMarket[F[_]] {
  def addStock(company: String, price: Long, count: Long): F[Unit]
  def getStock(company: String): F[StockMarket.StockInfo]
  def buyStock(company: String, count: Long): F[Unit]
  def sellStock(company: String, count: Long): F[Unit]
  def changePrice(company: String, newPrice: Long): F[Unit]
}

object StockMarket {
  final case class BadRequest(message: String) extends Throwable {
    override def getMessage(): String = message
  }

  final case class StockInfo(price: Long, count: Long)

  object StockInfo {
    implicit val stockInfoEncoder: Encoder[StockInfo] = new Encoder[StockInfo] {
      final def apply(stockInfo: StockInfo): Json = Json.obj(
        ("price", Json.fromLong(stockInfo.price)),
        ("count", Json.fromLong(stockInfo.count))
      )
    }
    implicit def stockInfoEntityEncoder[F[_]]: EntityEncoder[F, StockInfo] =
      jsonEncoderOf[F, StockInfo]
  }

  def impl[F[_]: Sync]: StockMarket[F] =
    new StockMarket[F] {
      private val stocks: Map[String, StockInfo] = Map()

      override def addStock(
          company: String,
          price: Long,
          count: Long
      ): F[Unit] =
        if (stocks.contains(company))
          BadRequest("Company already exists").raiseError[F, Unit]
        else
          stocks.update(company, StockInfo(price, count)).pure[F]

      override def getStock(company: String): F[StockInfo] =
        stocks.get(company) match {
          case None =>
            BadRequest(s"Company $company doesn't exist")
              .raiseError[F, StockInfo]
          case Some(stockInfo) =>
            stockInfo.pure[F]
        }

      override def buyStock(company: String, count: Long): F[Unit] = 
        stocks.get(company) match {
            case None => BadRequest(s"Company $company doesn't exist").raiseError[F, Unit]
            case Some(StockInfo(price, stockCount)) if stockCount >= count =>
                stocks.update(company, StockInfo(price, stockCount - count)).pure[F]
            case _ =>
                BadRequest(s"Not enough stock for $company").raiseError[F, Unit]
        }

      override def sellStock(company: String, count: Long): F[Unit] = 
        stocks.get(company) match {
            case None => BadRequest(s"Company $company doesn't exist").raiseError[F, Unit]
            case Some(StockInfo(price, stockCount)) =>
                stocks.update(company, StockInfo(price, stockCount + count)).pure[F]
        }

      override def changePrice(company: String, newPrice: Long): F[Unit] = 
        stocks.get(company) match {
            case None => BadRequest(s"Company $company doesn't exist").raiseError[F, Unit]
            case Some(StockInfo(_, stockCount)) =>
                stocks.update(company, StockInfo(newPrice, stockCount)).pure[F]
        }
    }
}
