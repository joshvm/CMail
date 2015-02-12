package cats.mail.server.former

import cats.net.core.data.former.DataFormer
import cats.net.core.data.former.DataFormer.Former
import cats.net.core.data.Data
import cats.mail.server.misc.Opcodes

class PopupMsgFormer extends DataFormer{

  def getOpcodes = Array(Opcodes.POPUP_MSG)

  @Former def form(msg: String): Data = data.put("msg", msg)

}
