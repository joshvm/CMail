package cats.mail.client.comp;

import cats.mail.client.CMailClient;
import cats.mail.client.mail.Mail;
import cats.mail.client.utils.Utils;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MailViewer extends JPanel{

    public Mail mail;

    private final JTextField dateBox;
    private final JTextField fromBox;
    private final JTextField toBox;
    private final JTextField subjectBox;
    private final JTextArea bodyArea;

    public MailViewer(){
        super(new BorderLayout());

        dateBox = new JTextField();
        dateBox.setEditable(false);
        dateBox.setHorizontalAlignment(JLabel.CENTER);

        fromBox = new JTextField();
        fromBox.setEditable(false);
        fromBox.setHorizontalAlignment(JLabel.CENTER);

        toBox = new JTextField();
        toBox.setEditable(false);
        toBox.setHorizontalAlignment(JLabel.CENTER);

        subjectBox = new JTextField();
        subjectBox.setEditable(false);
        subjectBox.setHorizontalAlignment(JLabel.CENTER);

        final JPanel fields = new JPanel(new GridLayout(4, 1, 2, 2));
        fields.add(Utils.createFieldBox("DATE", dateBox));
        fields.add(Utils.createFieldBox("FROM", fromBox));
        fields.add(Utils.createFieldBox("TO", toBox));
        fields.add(Utils.createFieldBox("SUBJECT", subjectBox));

        bodyArea = new JTextArea();
        bodyArea.setEditable(false);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);

        final JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(fields), new JScrollPane(bodyArea));

        add(split, BorderLayout.CENTER);
    }

    public void set(final Mail mail){
        this.mail = mail;
        dateBox.setText(mail == null ? "" : mail.date.toString());
        fromBox.setText(mail == null ? "" : mail.from);
        toBox.setText(mail == null ? "" : mail.to);
        subjectBox.setText(mail == null ? "" : mail.subject);
        bodyArea.setText(mail == null ? "" : mail.body);
        SwingUtilities.invokeLater(CMailClient.toolbar::update);
    }
}
