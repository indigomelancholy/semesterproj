import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class TeacherMain extends JFrame {
    private JLabel nameLabel;
    private JButton studentManagementButton;
    private JButton createCourseButton;
    private JPanel panel;
    private JLabel photolabel;

    public TeacherMain() {
        setSize(400, 400);
        setContentPane(panel);
        String firstName = User.getFirstName();
        String lastName = User.getLastName();
        InputStream imgStream = User.getPhotoStream();

        nameLabel.setText("Logged in as: " + User.getFirstName() + " " + User.getLastName());

        if (imgStream != null) {
            try {
                BufferedImage userImage = ImageIO.read(imgStream);
                Image scaledImg = userImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                photolabel.setIcon(new ImageIcon(scaledImg));
            } catch (IOException e) {
                System.out.println("Error loading user photo: " + e.getMessage());
                photolabel.setText("No Photo");
            }
        } else {
            photolabel.setText("No Photo");
        }

        setVisible(true);

        studentManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
new StudentManagement();
            }
        });

        createCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
}
