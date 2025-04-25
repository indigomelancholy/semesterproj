import javax.swing.*;

public class CourseManagement extends JFrame{
    private JTextField searchbar;
    private JTable table1;
    private JButton backButton;
    private JButton viewButton;
    private JButton enrollButton;
    private JButton dropButton;
    private JPanel panel;

    public CourseManagement(){
        setSize(400, 400);
        setContentPane(panel);
        setVisible(true);
    }
}
