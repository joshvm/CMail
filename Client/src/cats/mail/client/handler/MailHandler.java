package cats.mail.client.handler;

import cats.mail.client.CMailClient;
import cats.mail.client.Client;
import cats.mail.client.mail.Mail;
import cats.mail.client.misc.Opcodes;
import cats.net.client.handler.ClientDataHandler;
import cats.net.core.data.Data;
import java.util.Date;
import javax.swing.SwingUtilities;

public class MailHandler extends ClientDataHandler<Client>{

    public short[] getOpcodes(){
        return new short[]{Opcodes.MAIL};
    }

    public void handle(final Client client, final Data data){
        final long id = data.getLong("id");
        final String from = data.getString("from");
        final String to = data.getString("to");
        final String subject = data.getString("subject");
        final String body = data.getString("body");
        final Date date = data.get("date", Date.class);
        final boolean read = data.getBoolean("read");
        final Mail mail = new Mail(from, to, subject, body, date, id, read);
        if(CMailClient.mail.contains(mail))
            return;
        CMailClient.mail.add(mail);
        SwingUtilities.invokeLater(() -> CMailClient.mailList.push(mail));
    }
}
