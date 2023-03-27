package com.example.accounts

import cats.effect.Concurrent
import cats.implicits._
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto._
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.circe._
import org.http4s.Method._
import scala.collection.mutable.{Map => Map}

trait Accounts[F[_]] {
  def addAccount(login: String): F[Unit]
  def topUpAccount(login: String, amount: Long): F[Unit]
  def getStocks(login: String): F[List[(String, Accounts.StockInfo)]]
  def getTotal(login: String): F[Long]
  def buy(login: String, company: String, count: Long): F[Unit]
  def sell(login: String, company: String, count: Long): F[Unit]
}

object Accounts {
  def apply[F[_]](implicit ev: Accounts[F]): Accounts[F] = ev

  final case class BadRequest(message: String) extends Exception {
    override def getMessage(): String = message
  }

  final case class StockInfo(price: Long, count: Long)

  object StockInfo {
    implicit val stockInfoDecoder: Decoder[StockInfo] = deriveDecoder[StockInfo]
    implicit def stockInfoEntityDecoder[F[_]: Concurrent]
        : EntityDecoder[F, StockInfo] =
      jsonOf
    implicit val stockInfoEncoder: Encoder[StockInfo] = deriveEncoder[StockInfo]
    implicit def stockInfoEntityEncoder[F[_]]
        : EntityEncoder[F, List[(String, Accounts.StockInfo)]] =
      jsonEncoderOf
  }

  final case class Total(amount: Long)

  object Total {
    implicit val totalEncoder: Encoder[Total] = deriveEncoder[Total]
    implicit def totalEntityEncoder[F[_]]: EntityEncoder[F, Total] =
      jsonEncoderOf
  }

  def impl[F[_]: Concurrent](client: Client[F], host: Uri): Accounts[F] =
    new Accounts[F] {
      val dsl = new Http4sClientDsl[F] {}
      import dsl._

      val accounts: Map[String, Long] = Map()
      val stocks: Map[String, Map[String, Long]] = Map()

      def addAccount(login: String): F[Unit] =
        if (accounts.contains(login))
          BadRequest(s"Account $login already exists").raiseError[F, Unit]
        else
          {
            accounts.update(login, 0)
            stocks.update(login, Map())
          }.pure[F]

      def topUpAccount(login: String, amount: Long): F[Unit] =
        accounts.get(login) match {
          case None =>
            BadRequest(s"Account $login doesn't exist").raiseError[F, Unit]
          case Some(balance) => accounts.update(login, balance + amount).pure[F]
        }

      private def getStock(company: String): F[StockInfo] =
        client
          .expect[StockInfo](GET(host / "get-stock" / s"$company"))
          .adaptErr(e => BadRequest(e.getMessage()))

      def getStocks(login: String): F[List[(String, Accounts.StockInfo)]] =
        stocks.get(login) match {
          case None =>
            BadRequest(s"Account $login doesn't exist")
              .raiseError[F, List[(String, Accounts.StockInfo)]]
          case _ =>
            stocks
              .get(login)
              .fold[F[List[(String, StockInfo)]]](
                List[(String, StockInfo)]().pure[F]
              )(_.toList.traverse[F, (String, StockInfo)] {
                case (company, userCount) =>
                  for {
                    info <- getStock(company)
                  } yield (company, info.copy(count = userCount))
              })
        }

      private def getStockCount(login: String, company: String): Long =
        stocks.get(login).fold[Long](0)(st => st.getOrElse(company, 0))

      def getTotal(login: String): F[Long] =
        accounts.get(login) match {
          case None =>
            BadRequest(s"Account $login doesn't exist").raiseError[F, Long]
          case Some(balance) =>
            for {
              stocks <- getStocks(login)
            } yield stocks
              .map { case (company, info) => info.price * getStockCount(login, company) }
              .foldLeft[Long](0)(_ + _) + balance
        }

      def buy(login: String, company: String, count: Long): F[Unit] =
        accounts.get(login) match {
          case None =>
            BadRequest(s"Account $login doesn't exist").raiseError[F, Unit]
          case Some(balance) =>
            (for {
              stock <- getStock(company)
            } yield
              if (stock.price * count > balance)
                BadRequest(s"Not enough money").raiseError[F, Unit]
              else {
                (for {
                  _ <- client.expect[Unit](
                    POST(host / "buy-stock" / s"$company" / s"$count")
                  )
                  _ <- {
                    accounts.update(login, balance - stock.price * count)
                    stocks.updateWith(login) {
                      case None => None
                      case Some(st) =>
                        st.updateWith(company) {
                          case Some(value) => Some(value + count)
                          case None        => Some(count)
                        }
                        Some(st)
                    }
                  }
                    .pure[F]
                } yield ())
                  .adaptErr(e => BadRequest(e.getMessage()))
              }).flatten
        }

      def sell(login: String, company: String, count: Long): F[Unit] =
        accounts.get(login) match {
          case None =>
            BadRequest(s"Account $login doesn't exist").raiseError[F, Unit]
          case Some(balance) =>
            (for {
              stocks <- getStocks(login)
            } yield
              if (stocks.toMap.get(company).fold[Long](0)(_.count) < count)
                BadRequest(s"Not enough stocks").raiseError[F, Unit]
              else
                (for {
                  _ <- client.expect[Unit](
                    POST(host / "sell-stock" / s"$company" / s"$count")
                  )
                  _ <- {
                    val profit =
                      stocks.toMap.get(company).fold[Long](0)(_.price) * count
                    accounts.update(login, balance + profit)
                    this.stocks.updateWith(login) {
                      case None => None
                      case Some(st) =>
                        st.updateWith(company) {
                          case Some(value) => Some(value - count)
                          case None        => None
                        }
                        Some(st)
                    }
                  }
                    .pure[F]
                } yield ())
                  .adaptErr(e => BadRequest(e.getMessage()))).flatten
        }
    }
}
