package com.example.market

import cats.effect.Async
import com.comcast.ip4s._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger

object MarketServer {

  def run[F[_]: Async]: F[Nothing] = {
    val stockMarketAlg = StockMarket.impl[F]

    val httpApp = (
      MarketRoutes.marketRoutes[F](stockMarketAlg)
    ).orNotFound

    val finalHttpApp = Logger.httpApp(true, true)(httpApp)

    EmberServerBuilder
      .default[F]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(finalHttpApp)
      .build
  }.useForever
}
