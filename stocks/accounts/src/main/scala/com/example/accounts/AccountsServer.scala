package com.example.accounts

import cats.effect.Async
import com.comcast.ip4s._
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger

object AccountsServer {

  def run[F[_]: Async]: F[Nothing] = {
    for {
      client <- EmberClientBuilder.default[F].build
      host = uri"https://localhost:8080"
      accountsAlg = Accounts.impl[F](client, host)

      httpApp = (
        AccountsRoutes.accountsRoutes[F](accountsAlg)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      _ <- 
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8081")
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }.useForever
}
