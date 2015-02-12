package cats.mail.client;

import cats.net.client.BlockingClient;
import cats.net.core.Core;
import java.io.File;

public class Client extends BlockingClient {

    public Client(){
        super(4595);
    }

    public void init(){
        Core.addDataFormers(CMailClient.class.getResourceAsStream("/res/xml/formers.xml"));
        addHandlers(CMailClient.class.getResourceAsStream("/res/xml/handlers.xml"));
    }
}
