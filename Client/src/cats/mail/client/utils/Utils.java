package cats.mail.client.utils;

import cats.mail.client.CMailClient;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public final class Utils {

    private Utils(){}

    public static void msg(final String msg){
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, msg));
    }

    public static ImageIcon icon(final String name){
        return new ImageIcon(CMailClient.class.getResource("/res/img/" + name + ".png"));
    }

    public static Image img(final String name){
        return icon(name).getImage();
    }

    public static JPanel createFieldBox(final String field, final JTextField box){
        final JPanel panel = new JPanel(new BorderLayout());
        final JLabel label = new JLabel(field, JLabel.RIGHT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        label.setPreferredSize(new Dimension(60, label.getPreferredSize().height));
        panel.add(label, BorderLayout.WEST);
        panel.add(box, BorderLayout.CENTER);
        return panel;
    }
}
