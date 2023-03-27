package com.example.accounts

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  val run = AccountsServer.run[IO]
}
