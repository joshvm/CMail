package cats.mail.server.handler

import cats.mail.server.Server
import cats.mail.server.account.Account
import cats.mail.server.mail.Mail
import cats.mail.server.manager.AccountManager
import cats.mail.server.misc.Opcodes
import cats.net.core.data.Data
import cats.net.server.ClientConnection
import cats.net.server.handler.ServerDataHandler

class DeleteMailHandler extends ServerDataHandler[Server]{

  def getOpcodes = Array(Opcodes.DELETE_MAIL)

  def handle(server: Server, con: ClientConnection, data: Data){
    val acc: Account = AccountManager get con
    val id: Long = data getLong "id"
    val mail: Mail = acc getMail id
    if(mail == null){
      con.send(Opcodes.POPUP_MSG, s"cannot delete mail with id $id: invalid id")
      return
    }
    if(!mail.from.equals(acc) && !mail.to.equals(acc))
      return
    AccountManager.save()
  }

  override def handleException(server: Server, con: ClientConnection, data: Data, ex: Exception){
    con.send(Opcodes.POPUP_MSG, s"error deleting mail with id ${data.getLong("id")}")
  }

}
