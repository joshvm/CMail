package cats.mail.server.former

import cats.net.core.data.former.DataFormer
import cats.net.core.data.Data
import cats.mail.server.account.Account
import cats.net.core.data.former.DataFormer.Former
import cats.mail.server.misc.Opcodes

class AccountFormer extends DataFormer{

  def getOpcodes = Array(Opcodes.INIT)

  @Former def form(acc: Account): Data = {
    data.put("email", acc.email)
  }

}
