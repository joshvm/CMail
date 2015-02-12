package cats.mail.server.handler

import cats.mail.server.Server
import cats.mail.server.account.Account
import cats.mail.server.mail.Mail
import cats.mail.server.manager.AccountManager
import cats.mail.server.misc.{Opcodes, Constants}
import cats.net.core.data.Data
import cats.net.server.ClientConnection
import cats.net.server.handler.ServerDataHandler

class RegisterHandler extends ServerDataHandler[Server]{

  def getOpcodes = Array(Opcodes.REGISTER)

  def handle(server: Server, con: ClientConnection, data: Data){
    var login: String = data.getString("login").trim
    if(login contains Constants.DOMAIN)
      login = login.replace(Constants.DOMAIN, "")
    val pass: String = data.getString("pass").trim
    if(!validateLogin(con, login) || !validatePass(con, pass))
      return
    val acc: Account = new Account(login, pass)
    AccountManager ++ acc
    val welcome: Mail = new Mail(Server.Account.email, acc.email, "Welcome to CMail", "Welcome to CMail\n\nSent from CMail admin")
    acc addMail welcome
    con.send(Opcodes.POPUP_MSG, s"${acc.email} successfully registered")
    AccountManager.save()
  }

  override def handleException(server: Server, con: ClientConnection, data: Data, ex: Exception){
    con.send(Opcodes.POPUP_MSG, s"Error registering ${data.getString("login").trim}: $ex")
  }

  private def validateCommon(con: ClientConnection, prefix: String, str: String): Boolean = {
    if(str contains " "){
      con.send(Opcodes.POPUP_MSG, s"$prefix cannot contain any spaces")
      return false
    }
    if(str.length < 3 || str.length > 20){
      con.send(Opcodes.POPUP_MSG, s"$prefix must be inbetween 3 and 20 characters")
      return false
    }
    true
  }

  private def validateLogin(con: ClientConnection, login: String): Boolean = {
    if(!validateCommon(con, "login", login))
      return false
    if(login.equalsIgnoreCase(Server.Account.login)){
      con.send(Opcodes.POPUP_MSG, s"$login is reserved")
      return false
    }
    if(AccountManager contains login){
      con.send(Opcodes.POPUP_MSG, s"$login is already taken")
      return false
    }
    if(login contains "@"){
      con.send(Opcodes.POPUP_MSG, "login cannot contain an @")
      return false
    }
    true
  }

  private def validatePass(con: ClientConnection, pass: String): Boolean = {
    if(!validateCommon(con, "pass", pass))
      return false
    if(count(pass, Character.isDigit) < 1){
      con.send(Opcodes.POPUP_MSG, "pass must contain at least 1 digit")
      return false
    }
    if(count(pass, Character.isUpperCase) < 1){
      con.send(Opcodes.POPUP_MSG, "pass must contain at least 1 upper case character")
      return false
    }
    if(count(pass, Character.isLowerCase) < 1){
      con.send(Opcodes.POPUP_MSG, "pass must contain at least 1 lower case character")
      return false
    }
    if(count(pass, !_.isLetterOrDigit) < 1){
      con.send(Opcodes.POPUP_MSG, "pass must contain at least 1 symbol")
      return false
    }
    true
  }

  private def count(str: String, func: (Char) => Boolean): Int = {
    var count = 0
    for(c <- str.toCharArray if func(c))
      count += 1
    count
  }

}
