package cats.mail.server.former

import cats.net.core.data.former.DataFormer
import cats.net.core.data.former.DataFormer.Former
import cats.net.core.data.Data
import cats.mail.server.mail.Mail
import cats.mail.server.misc.Opcodes

class MailFormer extends DataFormer{

  def getOpcodes = Array(Opcodes.SEND_MAIL, Opcodes.DELETE_MAIL, Opcodes.MAIL)

  @Former def form(mail: Mail): Data = {
    data.put("id", mail.id)
    if(data.opcode == Opcodes.DELETE_MAIL)
      return data
    data.put("from", mail._from)
    .put("to", mail._to)
    .put("subject", mail.subject)
    .put("body", mail.body)
    .put("date", mail.date)
    .put("read", mail.read)
  }

  @Former def form(id: Long): Data = data.put("id", id)

}
