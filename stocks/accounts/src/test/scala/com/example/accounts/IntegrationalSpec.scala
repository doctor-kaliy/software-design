package com.example.accounts

import cats.effect.IO
import org.http4s.implicits._
import munit.CatsEffectSuite
import org.scalatest.wordspec.AnyWordSpecLike
import com.dimafeng.testcontainers.FixedHostPortGenericContainer
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.Method._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.client.Client
import org.http4s.Uri

class IntegrationalSpec extends CatsEffectSuite with AnyWordSpecLike {
  val dsl = new Http4sClientDsl[IO] {}
  import dsl._

  private def testIO(f: ((Client[IO], Uri, Accounts[IO])) => IO[Any]): Any = {
    ((for {
      client <- EmberClientBuilder.default[IO].build
      host = uri"http://localhost:8080"
    } yield (client, host))
      .use { case (client, host) =>
        f((client, host, Accounts.impl(client, host)))
      })
      .unsafeRunSync()
  }

  val container =
    FixedHostPortGenericContainer(
      imageName = "marketimage:latest",
      exposedPorts = Seq(8080),
      exposedHostPort = 8080,
      exposedContainerPort = 8080
    )

  "Account service" must {
    container.start()

    "satisfy integrational tests" in testIO({ case (client, host, accounts) =>
      for {
        // total without price changes
        _ <- assertIO(
          for {
            _ <- client.expect[Unit](
              POST(host / "add-stock" / "apple" / "1" / "100")
            )
            _ <- accounts.addAccount("user1")
            _ <- accounts.topUpAccount("user1", 100)
            _ <- accounts.buy("user1", "apple", 100)
            total <- accounts.getTotal("user1")
          } yield total,
          100L
        )
        // total with price changes
        _ <- assertIO(
          for {
            _ <- client.expect[Unit](
              POST(host / "change-price" / "apple" / "2")
            )
            total <- accounts.getTotal("user1")
          } yield total,
          200L
        )

        // not enough stocks to sell
        _ <- interceptIO[Accounts.BadRequest](
          for {
            _ <- accounts.sell("user1", "apple", 200)
          } yield ()
        )

        // not enough stocks to buy
        _ <- interceptIO[Accounts.BadRequest](
          for {
            _ <- accounts.buy("user1", "apple", 1)
          } yield ()
        )

        // not enough money to buy stocks
        _ <- interceptIO[Accounts.BadRequest](
          for {
            _ <- client.expect[Unit](
              POST(host / "add-stock" / "yandex" / "1" / "100")
            )
            _ <- accounts.buy("user1", "yandex", 1)
          } yield ()
        )

        // get stocks
        _ <- assertIO(
          for {
            _ <- client.expect[Unit](
              POST(host / "add-stock" / "microsoft" / "1" / "100")
            )
            _ <- accounts.sell("user1", "apple", 50)
            _ <- accounts.buy("user1", "microsoft", 100)
            _ <- client.expect[Unit](
              POST(host / "change-price" / "microsoft" / "3")
            )
            stocks <- accounts.getStocks("user1")
          } yield stocks.sortBy(_._1),
          List(("microsoft", Accounts.StockInfo(3, 100)), ("apple", Accounts.StockInfo(2, 50))).sortBy(_._1)
        )
      } yield ()
    })

  }

}
