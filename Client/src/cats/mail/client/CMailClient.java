package cats.mail.client;

import cats.mail.client.auth.AuthWindow;
import cats.mail.client.comp.MailList;
import cats.mail.client.comp.MailViewer;
import cats.mail.client.comp.SendMailDialog;
import cats.mail.client.mail.Mail;
import cats.mail.client.misc.Constants;
import cats.mail.client.misc.Opcodes;
import cats.mail.client.utils.Utils;
import cats.net.core.data.Data;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.DustSkin;

public class CMailClient extends JFrame {

    public class ToolBar extends JToolBar implements ActionListener {

        private final JButton logoutButton;
        private final JButton composeButton;

        private final JButton deleteButton;
        private final JButton replyButton;

        private ToolBar(){
            setFloatable(false);
            setOrientation(JToolBar.HORIZONTAL);
            setLayout(new BorderLayout());

            logoutButton = new JButton("Logout", Utils.icon("logout_24"));
            logoutButton.addActionListener(this);

            composeButton = new JButton("Compose", Utils.icon("mail_compose_24"));
            composeButton.addActionListener(this);

            deleteButton = new JButton("Delete", Utils.icon("mail_delete_24"));
            deleteButton.setEnabled(false);
            deleteButton.addActionListener(this);

            replyButton = new JButton("Reply", Utils.icon("mail_reply_24"));
            replyButton.setEnabled(false);
            replyButton.addActionListener(this);

            final JPanel first = new JPanel(new GridLayout(1, 2, 2, 0));
            first.add(composeButton);
            first.add(logoutButton);

            final JPanel second = new JPanel(new GridLayout(1, 2, 2, 0));
            second.add(replyButton);
            second.add(deleteButton);

            add(first, BorderLayout.WEST);
            add(second, BorderLayout.EAST);
        }

        public void update(){
            deleteButton.setEnabled(viewer.mail != null);
            replyButton.setEnabled(viewer.mail != null);
        }

        public void actionPerformed(final ActionEvent e){
            final Object source = e.getSource();
            if(source.equals(logoutButton)){
                send(new Data(Opcodes.LOGOUT));
                startAuth();
            }else if(source.equals(composeButton)){
                SendMailDialog.display();
            }else if(source.equals(deleteButton)){
                CMailClient.mail.remove(mail);
                SwingUtilities.invokeLater(
                        () -> {
                            CMailClient.mailList.remove(viewer.mail);
                            CMailClient.viewer.set(null);
                        }
                );
                CMailClient.send(Opcodes.DELETE_MAIL, mail);
            }else if(source.equals(replyButton)){
                SendMailDialog.display(viewer.mail);
            }
        }
    }

    public static String email;

    public static Client client;

    public static AuthWindow auth;
    public static CMailClient instance;

    public static List<Mail> mail = new ArrayList<>();

    public static MailList mailList;
    public static MailViewer viewer;

    public static ToolBar toolbar;

    static{
        client = new Client();
        client.start();

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> send(new Data(Opcodes.LOGOUT)))
        );
    }

    public CMailClient(){
        super(String.format("%s - %s", Constants.APP_NAME, email));
        setIconImage(Utils.img("cmail"));
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        mailList = new MailList();

        viewer = new MailViewer();

        final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mailList, viewer);

        add(toolbar = new ToolBar(), BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    public static void send(final short opcode, final Object... args){
        if(!client.isConnected())
            client.reconnect();
        if(!client.isConnected()){
            Utils.msg("Not connected");
            return;
        }
        client.send(opcode, args);
    }

    public static void send(final Data data){
        if(!client.isConnected())
            client.reconnect();
        if(!client.isConnected()){
            Utils.msg("Not connected");
            return;
        }
        client.send(data);
    }

    public static void startAuth(){
        if(instance != null && instance.isVisible())
            instance.dispose();
        SwingUtilities.invokeLater(
                () -> {
                    auth = new AuthWindow();
                    auth.setVisible(true);
                }
        );
    }

    public static void startMail(){
        if(auth != null && auth.isVisible())
            auth.dispose();
        SwingUtilities.invokeLater(
                () -> {
                    instance = new CMailClient();
                    instance.setVisible(true);
                }
        );
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(
                () -> {
                    JFrame.setDefaultLookAndFeelDecorated(true);
                    JDialog.setDefaultLookAndFeelDecorated(true);
                    SubstanceLookAndFeel.setSkin(DustSkin.class.getName());
                    startAuth();
                }
        );
    }
}
