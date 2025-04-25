import javax.swing.*;

public class TeacherMain extends JFrame {
    private JLabel nameLabel;
    private JButton studentManagementButton;
    private JButton courseManagementButton;
    private JPanel panel;

    public TeacherMain() {
        setSize(400, 400);
        setContentPane(panel);
        setVisible(true);
        nameLabel.setText("Logged in as: " + User.getFirstName() + " " + User.getLastName());

    }
}
