package cats.mail.client.auth;

import cats.mail.client.CMailClient;
import cats.mail.client.misc.Constants;
import cats.mail.client.misc.Opcodes;
import cats.mail.client.utils.Utils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class AuthWindow extends JFrame implements ActionListener {

    private final JTextField loginBox;
    private final JPasswordField passBox;
    private final JButton loginButton;
    private final JButton registerButton;

    public AuthWindow(){
        super(String.format("%s - Register/Login", Constants.APP_NAME));
        setIconImage(Utils.img("cmail"));
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        loginBox = new JTextField();

        passBox = new JPasswordField();

        final JPanel fields = new JPanel(new GridLayout(4, 1, 0, 2));
        fields.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        fields.add(new JLabel("Login"));
        fields.add(loginBox);
        fields.add(new JLabel("Password"));
        fields.add(passBox);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);

        registerButton = new JButton("Register");
        registerButton.addActionListener(this);

        final JPanel buttons = new JPanel(new GridLayout(1, 2, 5, 0));
        buttons.add(loginButton);
        buttons.add(registerButton);

        final JPanel south = new JPanel(new GridLayout(1, 2));
        south.add(Box.createHorizontalBox());
        south.add(buttons);

        final JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        container.add(new JLabel(Utils.icon("cmail")), BorderLayout.NORTH);
        container.add(fields, BorderLayout.CENTER);
        container.add(south, BorderLayout.SOUTH);

        add(container, BorderLayout.CENTER);

        setSize(new Dimension(300, getPreferredSize().height));
        setLocationRelativeTo(null);
    }

    public void actionPerformed(final ActionEvent e){
        final Object source = e.getSource();
        if(source.equals(loginButton) || source.equals(registerButton)){
            final String login = loginBox.getText().trim();
            final String pass = new String(passBox.getPassword()).trim();
            if(login.isEmpty() || pass.isEmpty()){
                Utils.msg("Fill in all fields");
                return;
            }
            final short opcode = source.equals(loginButton) ? Opcodes.LOGIN : Opcodes.REGISTER;
            CMailClient.send(opcode, login, pass);
        }
    }
}
