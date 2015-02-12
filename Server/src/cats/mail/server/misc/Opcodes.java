package cats.mail.server.misc;

public final class Opcodes {

    public static final short REGISTER = 0;
    public static final short LOGIN = 1;

    public static final short POPUP_MSG = 2;

    public static final short SEND_MAIL = 3;
    public static final short DELETE_MAIL = 4;
    public static final short MAIL = 5;

    public static final short INIT = 6;

    public static final short READ_MAIL = 7;

    public static final short LOGOUT = 8;

    private Opcodes(){}
}
