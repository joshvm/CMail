package cats.mail.server.manager

import cats.mail.server.account.Account
import cats.net.server.ClientConnection

object AccountManager extends Manager[String, Account]("accounts"){

  protected def key(acc: Account): String = acc.login.toLowerCase

  def get(con: ClientConnection): Account = {
    val acc: Account = con.attachment[Account]
    if(acc == null)
      null
    else
      get(acc.login.toLowerCase)
  }

}
