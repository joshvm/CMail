package cats.mail.server.handler

import cats.mail.server.Server
import cats.net.core.data.Data
import cats.net.server.ClientConnection
import cats.net.server.handler.ServerDataHandler
import cats.mail.server.manager.AccountManager
import cats.mail.server.account.Account
import cats.mail.server.misc.{Opcodes, Constants}

class LoginHandler extends ServerDataHandler[Server]{

  def getOpcodes = Array(Opcodes.LOGIN)

  def handle(server: Server, con: ClientConnection, data: Data){
    var login: String = data.getString("login").trim
    if(login contains Constants.DOMAIN)
      login = login.replace(Constants.DOMAIN, "")
    val pass: String = data.getString("pass").trim
    val acc: Account = AccountManager get login.toLowerCase
    if(acc == null){
      con.send(Opcodes.POPUP_MSG, s"$login doesn't exist")
      return
    }
    if(!acc.pass.equals(pass)){
      con.send(Opcodes.POPUP_MSG, s"password mismatch for $login")
      return
    }
    if(acc.online){
      con.send(Opcodes.POPUP_MSG, s"$login is already logged in")
      return
    }
    acc.con = con
    con attach acc
    acc.send(Opcodes.INIT, acc)
    acc.mail.values.foreach(con.send(Opcodes.MAIL, _))
  }

}
