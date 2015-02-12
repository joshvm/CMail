package cats.mail.client.comp;

import cats.mail.client.CMailClient;
import cats.mail.client.mail.Mail;
import cats.mail.client.misc.Opcodes;
import cats.mail.client.utils.Utils;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SendMailDialog extends JFrame {

    private SendMailDialog(final Mail mail){
        setIconImage(Utils.img("cmail"));
        if(mail != null)
            setTitle(String.format("[%s] Reply to: %s", mail.from, mail.subject));
        else
            setTitle(String.format("[%s] Compose New Mail", CMailClient.email));

        final JTextField toBox = new JTextField(mail != null ? mail.from : "");
        toBox.setEditable(mail == null);
        toBox.setHorizontalAlignment(JLabel.CENTER);

        final JTextField subjectBox = new JTextField(mail != null ? String.format("[Reply-To] %s", mail.subject) : "Untitled");
        subjectBox.setHorizontalAlignment(JLabel.CENTER);

        final JPanel fields = new JPanel(new GridLayout(2, 1, 2, 2));
        fields.add(Utils.createFieldBox("TO", toBox));
        fields.add(Utils.createFieldBox("SUBJECT", subjectBox));

        final JTextArea bodyArea = new JTextArea();
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);

        final JPanel center = new JPanel(new BorderLayout());
        center.add(fields, BorderLayout.NORTH);
        center.add(new JScrollPane(bodyArea));

        final JButton sendButton = new JButton("Send", Utils.icon("mail_send_16"));
        sendButton.addActionListener(
                e -> {
                    final String to = toBox.getText().trim();
                    final String subject = subjectBox.getText().trim();
                    final String body = bodyArea.getText().trim();
                    if(to.isEmpty() || body.isEmpty()){
                        Utils.msg("Must have a to and a body");
                        return;
                    }
                    CMailClient.send(Opcodes.SEND_MAIL, to, subject.isEmpty() ? "Untitled" : subject, body);
                    dispose();
                }
        );

        final JPanel options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.X_AXIS));
        options.add(sendButton);

        add(center, BorderLayout.CENTER);
        add(sendButton, BorderLayout.SOUTH);
    }

    public static void display(final Mail mail){
        final SendMailDialog dialog = new SendMailDialog(mail);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static void display(){
        display(null);
    }
}
