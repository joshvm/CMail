package cats.mail.client.former;

import cats.mail.client.misc.Opcodes;
import cats.net.core.data.Data;
import cats.net.core.data.former.DataFormer;

public class SendMailFormer extends DataFormer {

    public short[] getOpcodes(){
        return new short[]{Opcodes.SEND_MAIL};
    }

    @Former
    public Data form(final String to, final String subject, final String body){
        return data.put("to", to).put("subject", subject).put("body", body);
    }
}
