package com.example.market

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object MarketRoutes {

  def marketRoutes[F[_]: Sync](stockMarket: StockMarket[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {
      case POST -> Root / "add-stock" / company / LongVar(price) / LongVar(
            count
          ) =>
        stockMarket
          .addStock(company, price, count)
          .redeemWith(err => dsl.BadRequest(err.getMessage()), _ => Ok())

      case GET -> Root / "get-stock" / company =>
        stockMarket
          .getStock(company)
          .redeemWith(err => dsl.BadRequest(err.getMessage()), Ok(_))

      case POST -> Root / "buy-stock" / company / LongVar(count) =>
        stockMarket
          .buyStock(company, count)
          .redeemWith(err => dsl.BadRequest(err.getMessage()), _ => Ok())

      case POST -> Root / "sell-stock" / company / LongVar(count) =>
        stockMarket
          .sellStock(company, count)
          .redeemWith(err => dsl.BadRequest(err.getMessage()), _ => Ok())

      case POST -> Root / "change-price" / company / LongVar(newPrice) =>
        stockMarket
          .changePrice(company, newPrice)
          .redeemWith(err => dsl.BadRequest(err.getMessage()), _ => Ok())
    }
  }
}
