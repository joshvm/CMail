package cats.mail.client.former;

import cats.mail.client.misc.Opcodes;
import cats.net.core.data.Data;
import cats.net.core.data.former.DataFormer;

public class AuthFormer extends DataFormer {

    public short[] getOpcodes(){
        return new short[]{Opcodes.REGISTER, Opcodes.LOGIN};
    }

    @Former
    public Data form(final String login, final String pass){
        return data.put("login", login).put("pass", pass);
    }
}
