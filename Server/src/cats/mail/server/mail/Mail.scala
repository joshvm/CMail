package cats.mail.server.mail

import java.util.Date
import cats.net.core.buffer.{Buffer, BufferBuilder}
import cats.net.core.codec.{Decoder, Encoder}
import cats.net.core.Core
import cats.mail.server.manager.AccountManager
import cats.mail.server.account.Account
import cats.mail.server.misc.Constants
import cats.mail.server.Server

class Mail(val _from: String, val _to: String, val subject: String, val body: String, val date: Date = new Date, val id: Long = System.nanoTime) {

  var read: Boolean = false
  def from: Account = if(_from.equalsIgnoreCase(Server.Account.email)) Server.Account else AccountManager get _from.replace(Constants.DOMAIN, "").toLowerCase
  def to: Account = if(_to.equalsIgnoreCase(Server.Account.email)) Server.Account else AccountManager get _to.replace(Constants.DOMAIN, "").toLowerCase

  override def toString: String = s"$id $from -> $to"
}

object Mail{

  private val Encoder: Encoder[Mail] = new Encoder[Mail]{
    def encode(bldr: BufferBuilder, mail: Mail){
      bldr putString mail._from
      bldr putString mail._to
      bldr putString mail.subject
      bldr putString mail.body
      bldr putObject mail.date
      bldr putLong mail.id
      bldr putBoolean mail.read
    }
  }

  private val Decoder: Decoder[Mail] = new Decoder[Mail]{
    def decode(buf: Buffer): Mail = {
      val mail: Mail = new Mail(buf.getString, buf.getString, buf.getString, buf.getString, buf.getObject[Date], buf.getLong)
      mail.read = buf.getBoolean
      mail
    }
  }

  def registerCodec() = Core.addCodec(classOf[Mail], Encoder, Decoder)

}
