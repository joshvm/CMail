package cats.mail.server.handler

import cats.mail.server.Server
import cats.mail.server.misc.Opcodes
import cats.net.server.handler.ServerDataHandler
import cats.net.core.data.Data
import cats.net.server.ClientConnection
import cats.mail.server.account.Account
import cats.mail.server.manager.AccountManager

class LogoutHandler extends ServerDataHandler[Server]{

  def getOpcodes = Array(Opcodes.LOGOUT)

  def handle(server: Server, con: ClientConnection, data: Data){
    val acc: Account = AccountManager get con
    if(!acc.online)
      return
    acc.con = null
    con attach null
  }

}
