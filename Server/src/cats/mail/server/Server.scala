package cats.mail.server

import cats.mail.server.account.Account
import cats.mail.server.mail.Mail
import cats.mail.server.manager.AccountManager
import cats.net.core.Core
import cats.net.server.event.ServerListener
import cats.net.server.{ClientConnection, NonBlockingServer}
import java.io.File
import java.util.Comparator

class Server extends NonBlockingServer(4595) with ServerListener[Server]{

  def init(){
    Account.registerCodec()
    Mail.registerCodec()
    AccountManager.load()
    Core.addDataFormers(new File("./Server/res/xml/formers.xml"))
    addHandlers(new File("./Server/res/xml/handlers.xml"))
    addListener(this)
  }

  def onLeave(server: Server, con: ClientConnection){
    val acc: Account = AccountManager get con
    if(acc == null || !acc.online)
      return
    acc.con = null
    con attach null
  }

  def onJoin(server: Server, con: ClientConnection){}

}

object Server{
  def Account: Account = new Account("admin", "admin")

  def main(args: Array[String]){
    new Server().start()
  }
}
