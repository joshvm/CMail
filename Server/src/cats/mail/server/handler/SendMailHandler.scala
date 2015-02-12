package cats.mail.server.handler

import cats.mail.server.Server
import cats.mail.server.account.Account
import cats.mail.server.mail.Mail
import cats.mail.server.manager.AccountManager
import cats.mail.server.misc.{Opcodes, Constants}
import cats.net.core.data.Data
import cats.net.server.ClientConnection
import cats.net.server.handler.ServerDataHandler

class SendMailHandler extends ServerDataHandler[Server]{

  def getOpcodes = Array(Opcodes.SEND_MAIL)

  def handle(server: Server, con: ClientConnection, data: Data){
    var toLogin: String = data.getString("to").trim
    if(toLogin contains Constants.DOMAIN)
      toLogin = toLogin.replace(Constants.DOMAIN, "")
    val to: Account = AccountManager get toLogin
    if(to == null){
      con.send(Opcodes.POPUP_MSG, s"can't send mail to $to. doesn't exist")
      return
    }
    var subject: String = data.getString("subject").trim
    if(subject.isEmpty)
      subject = "No subject"
    val body: String = data.getString("body").trim
    if(body.isEmpty){
      con.send(Opcodes.POPUP_MSG, s"can't send msg to ${to.email}, body is empty")
      return
    }
    val from: Account = AccountManager get con
    val mail: Mail = new Mail(from.email, to.email, subject, body)
    from addMail mail
    to addMail mail
    AccountManager.save()
    from.send(Opcodes.MAIL, mail)
    to.send(Opcodes.MAIL, mail)
  }

  override def handleException(server: Server, con: ClientConnection, data: Data, ex: Exception){
    con.send(Opcodes.POPUP_MSG, s"Error sending mail to ${data.getString("to").trim}: $ex")
  }
}
