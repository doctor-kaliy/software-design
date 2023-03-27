package com.example.market

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  val run = MarketServer.run[IO]
}
