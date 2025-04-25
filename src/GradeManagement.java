import javax.swing.*;

public class GradeManagement extends JFrame{
    private JTable table1;
    private JTextField searchbar;
    private JButton removeGradeButton;
    private JButton backButton;
    private JButton addGradeButton;
    private JButton calculateGPAButton;
    private JPanel panel;

    public GradeManagement() {
        setSize(400, 400);
        setContentPane(panel);
        setVisible(true);

    }
}
