package com.example.accounts

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

import com.example.accounts.Accounts._
import com.example.accounts.Accounts.StockInfo._
import com.example.accounts.Accounts.Total._

object AccountsRoutes {

  def accountsRoutes[F[_]: Sync](accounts: Accounts[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {
      case POST -> Root / "add-account" / login =>
        accounts
          .addAccount(login)
          .redeemWith(err => dsl.BadRequest(err.getMessage()), _ => Ok())

      case POST -> Root / "topup" / login / LongVar(amount) =>
        accounts
          .topUpAccount(login, amount)
          .redeemWith(err => dsl.BadRequest(err.getMessage()), _ => Ok())

      case GET -> Root / "get-stocks" / login =>
        accounts
          .getStocks(login)
          .redeemWith(err => dsl.BadRequest(err.getMessage()), Ok(_))

      case GET -> Root / "get-total" / login =>
        accounts
          .getTotal(login)
          .redeemWith(
            err => dsl.BadRequest(err.getMessage()),
            v => Ok(Total(v))
          )

      case POST -> Root / "buy" / login / company / LongVar(amount) =>
        accounts
          .buy(login, company, amount)
          .redeemWith(
            err => dsl.BadRequest(err.getMessage()),
            _ => Ok()
          )

      case POST -> Root / "sell" / login / company / LongVar(amount) =>
        accounts
          .sell(login, company, amount)
          .redeemWith(
            err => dsl.BadRequest(err.getMessage()),
            _ => Ok()
          )
    }
  }

}
