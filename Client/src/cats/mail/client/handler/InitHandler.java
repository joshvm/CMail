package cats.mail.client.handler;

import cats.mail.client.CMailClient;
import cats.mail.client.Client;
import cats.mail.client.misc.Constants;
import cats.mail.client.misc.Opcodes;
import cats.net.client.handler.ClientDataHandler;
import cats.net.core.data.Data;

public class InitHandler extends ClientDataHandler<Client> {

    public short[] getOpcodes(){
        return new short[]{Opcodes.INIT};
    }

    public void handle(final Client client, final Data data){
        CMailClient.email = data.getString("email");
        CMailClient.startMail();
    }
}
