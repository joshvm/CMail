package cats.mail.client.handler;

import cats.mail.client.Client;
import cats.mail.client.misc.Opcodes;
import cats.mail.client.utils.Utils;
import cats.net.client.handler.ClientDataHandler;
import cats.net.core.data.Data;

public class PopupHandler extends ClientDataHandler<Client> {

    public short[] getOpcodes(){
        return new short[]{Opcodes.POPUP_MSG};
    }

    public void handle(final Client client, final Data data){
        Utils.msg(data.getString("msg"));
    }
}
