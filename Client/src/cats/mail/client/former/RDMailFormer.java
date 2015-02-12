package cats.mail.client.former;

import cats.mail.client.mail.Mail;
import cats.mail.client.misc.Opcodes;
import cats.net.core.data.Data;
import cats.net.core.data.former.DataFormer;

public class RDMailFormer extends DataFormer {

    public short[] getOpcodes(){
        return new short[]{Opcodes.READ_MAIL, Opcodes.DELETE_MAIL};
    }

    @Former
    public Data form(final Mail mail){
        return data.put("id", mail.id);
    }
}
