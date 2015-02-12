package cats.mail.client.comp;

import cats.mail.client.CMailClient;
import cats.mail.client.mail.Mail;
import cats.mail.client.misc.Opcodes;
import cats.mail.client.utils.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TreeSet;
import java.util.function.Predicate;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

public class MailList extends JPanel {

    private class MailRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList l, Object o, int i, boolean s, boolean f){
            final Component c = super.getListCellRendererComponent(l, o, i, s, f);
            if(o == null)
                return c;
            final Mail m = (Mail) o;
            final JLabel label = (JLabel) c;
            final StringBuilder bldr = new StringBuilder();
            bldr.append("<html>");
            bldr.append("<font size=\"5\">").append(m.subject).append("</font>");
            bldr.append("<br>");
            bldr.append("<font size=\"4\">").append(m.from.equals(CMailClient.email) ? m.to : m.from).append("</font>");
            bldr.append("<br>");
            bldr.append("<font size=\"2\">").append("<em>").append(m.date).append("</em>").append("</font>");
            bldr.append("</html>");
            if(!m.read)
                label.setFont(label.getFont().deriveFont(Font.BOLD));
            label.setText(bldr.toString());
            label.setToolTipText(m.body);
            return label;
        }
    }

    private class MailModel extends AbstractListModel<Mail> {

        private final TreeSet<Mail> model = new TreeSet<>();

        public int getSize(){
            return model.size();
        }

        public Mail getElementAt(final int i){
            return (Mail) model.toArray()[i];
        }

        public void add(final Mail mail){
            model.add(mail);
            fireContentsChanged(this, 0, getSize());
        }

        public void remove(final Mail mail){
            model.remove(mail);
            fireContentsChanged(this, 0, getSize());
        }

        public void filter(final Predicate<Mail> condition){
            model.clear();
            CMailClient.mail.stream().filter(condition).forEach(model::add);
            fireContentsChanged(this, 0, getSize());
        }
    }

    private class FolderRenderer extends DefaultListCellRenderer{

        public Component getListCellRendererComponent(JList l, Object o, int i, boolean s, boolean f){
            final Component c = super.getListCellRendererComponent(l, o, i, s, f);
            if(o == null)
                return c;
            final Mail.Folder folder = (Mail.Folder) o;
            final long count = CMailClient.mail.stream().filter(folder.condition).count();
            final JLabel label = (JLabel) c;
            label.setText(String.format("%s (%d)", folder, count));
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setIcon(Utils.icon("page_folder_16"));
            return label;
        }
    }

    private final JComboBox<Mail.Folder> folders;

    private final JList<Mail> list;
    private final MailModel model;

    public Mail.Folder selectedFolder;

    public MailList(){
        super(new BorderLayout());

        model = new MailModel();

        list = new JList<>(model);
        list.setCellRenderer(new MailRenderer());
        list.addMouseListener(
                new MouseAdapter(){
                    public void mousePressed(final MouseEvent me){
                        if(me.getButton() != MouseEvent.BUTTON3)
                            return;
                        final int i = list.locationToIndex(me.getPoint());
                        if(i == -1)
                            return;
                        final Mail mail = model.getElementAt(i);
                        list.setSelectedValue(mail, true);
                        final JPopupMenu popup = new JPopupMenu();
                        final JMenuItem replyItem = new JMenuItem("Reply", Utils.icon("mail_reply_16"));
                        replyItem.addActionListener(
                                e -> {
                                    SendMailDialog.display(mail);
                                }
                        );
                        final JMenuItem deleteItem = new JMenuItem("Delete", Utils.icon("mail_delete_16"));
                        deleteItem.addActionListener(
                                e -> {
                                    CMailClient.mail.remove(mail);
                                    remove(mail);
                                    CMailClient.viewer.set(null);
                                    CMailClient.send(Opcodes.DELETE_MAIL, mail);
                                }
                        );
                        popup.add(replyItem);
                        popup.add(deleteItem);
                        popup.show(list, me.getX(), me.getY());
                    }
                }
        );
        list.addListSelectionListener(
                e -> {
                    final Mail mail = list.getSelectedValue();
                    if(mail != null && !mail.read){
                        mail.read = true;
                        mail.folder = Mail.Folder.RECEIVED;
                        CMailClient.send(Opcodes.READ_MAIL, mail);
                    }
                    CMailClient.viewer.set(mail);
                }
        );

        selectedFolder = Mail.Folder.INBOX;

        folders = new JComboBox<>(Mail.Folder.values());
        folders.setRenderer(new FolderRenderer());
        folders.addItemListener(
                e -> {
                    selectedFolder = (Mail.Folder) folders.getSelectedItem();
                    final int oldIndex = list.getSelectedIndex();
                    list.clearSelection();
                    model.filter(selectedFolder.condition);
                    if(oldIndex != -1 && oldIndex <= model.getSize()-1)
                        list.setSelectedIndex(oldIndex);
                    list.repaint();
                }
        );

        add(folders, BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    public void remove(final Mail mail){
        model.remove(mail);
        list.repaint();
    }

    public void push(final Mail mail){
        folders.repaint();
        if(mail.folder.equals(selectedFolder)){
            model.add(mail);
            list.repaint();
        }
    }
}
