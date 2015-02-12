package cats.mail.server.account

import cats.mail.server.mail.Mail
import cats.mail.server.misc.Constants
import cats.net.core.Core
import cats.net.core.buffer.{Buffer, BufferBuilder}
import cats.net.core.codec.{Decoder, Encoder}
import cats.net.server.ClientConnection
import scala.collection.mutable

class Account(val login: String, val pass: String) {

  var con: ClientConnection = null
  val email: String = s"$login${Constants.DOMAIN}"
  var mail: mutable.Map[Long, Mail] = mutable.Map()

  def addMail(m: Mail) = mail += m.id -> m

  def getMail(id: Long): Mail = {
    val opt: Option[Mail] = mail get id
    if(!opt.isDefined || opt.isEmpty)
      null.asInstanceOf[Mail]
    else
      opt.get
  }

  def online: Boolean = con != null

  def send(opcode: Short, args: Object*){
    if(!online)
      return
    con.send(opcode, args.toArray)
  }

  override def equals(o: Any): Boolean = {
    o.isInstanceOf[Account] && o.asInstanceOf[Account].login.equalsIgnoreCase(login)
  }

  override def toString: String = email

}

object Account{

  private val Encoder: Encoder[Account] = new Encoder[Account]{
    def encode(bldr: BufferBuilder, acc: Account){
      bldr putString acc.login
      bldr putString acc.pass
      bldr putInt acc.mail.size
      acc.mail.values foreach bldr.putObject
    }
  }

  private val Decoder: Decoder[Account] = new Decoder[Account]{
    def decode(buf: Buffer): Account = {
      val acc: Account = new Account(buf.getString, buf.getString)
      for(i <- 0 until buf.getInt)
        acc.addMail(buf.getObject[Mail])
      acc
    }
  }

  def registerCodec() = Core.addCodec(classOf[Account], Encoder, Decoder)

}
