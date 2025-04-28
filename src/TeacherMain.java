import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TeacherMain extends JFrame {
    private JLabel nameLabel;
    private JButton studentManagementButton;
    private JButton createCourseButton;
    private JPanel panel;
    private JLabel photolabel;
    private JLabel main;
    private JLabel mana;

    public TeacherMain() {
        setSize(900, 600);
        setContentPane(panel);
        panel.setBackground(new Color(241, 182, 238));

        Font font = new Font("Roboto", Font.PLAIN, 20);
        Font buttonFont = new Font("Cambria", Font.BOLD, 24);
        Font labelFont = new Font("Roboto", Font.BOLD, 18);

        main.setFont(font);
        main.setForeground(new Color(87, 40, 105));

        mana.setFont(font);
        mana.setForeground(new Color(87, 40, 105));


        nameLabel.setFont(labelFont);
        nameLabel.setForeground(new Color(115, 76, 187));
        nameLabel.setText("Logged in as: " + User.getFirstName() + " " + User.getLastName());

        studentManagementButton.setBackground(new Color(115, 76, 187));
        studentManagementButton.setForeground(Color.WHITE);
       studentManagementButton.setFont(buttonFont);
        studentManagementButton.setPreferredSize(new Dimension(250, 40));

        createCourseButton.setBackground(new Color(115, 76, 187));
        createCourseButton.setForeground(Color.WHITE);
        createCourseButton.setFont(buttonFont);
       createCourseButton.setPreferredSize(new Dimension(250, 40));


        String firstName = User.getFirstName();
        String lastName = User.getLastName();
        InputStream imgStream = User.getPhotoStream();



        if (imgStream != null) {
            try {
                BufferedImage userImage = ImageIO.read(imgStream);
                Image scaledImg = userImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                photolabel.setIcon(new ImageIcon(scaledImg));
                photolabel.setBorder(BorderFactory.createLineBorder(new Color(115, 76, 187), 4));

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
dispose();
            }
        });

        createCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String courseName = JOptionPane.showInputDialog(TeacherMain.this, "Enter the new course's name:");

                if (courseName != null && !courseName.trim().isEmpty()) {

                    try(Connection connection = connect.getConnection();
                        PreparedStatement stmt = connection.prepareStatement("SELECT MAX(CourseID) from Courses");
                        ResultSet rs = stmt.executeQuery()){

                            int newCourseID = 1;
                            if (rs.next()) {
                                newCourseID = rs.getInt(1) + 1;
                            }

                        try(PreparedStatement insertcourse = connection.prepareStatement("INSERT INTO Courses (CourseID, CourseName, TeacherID) VALUES (?, ?,?)")) {

                            insertcourse.setInt(1, newCourseID);
                            insertcourse.setString(2, courseName);
                            insertcourse.setInt(3, User.getTeacherID());

                            insertcourse.executeUpdate();

                            JOptionPane.showMessageDialog(TeacherMain.this, "Course created is: " + courseName + "(ID: " + newCourseID + ")");
                        }

                    }catch (Exception ex){
                            JOptionPane.showMessageDialog(TeacherMain.this, "Error: " + ex.getMessage());
                        }

                }else{
                    JOptionPane.showMessageDialog(TeacherMain.this, "Course name cant be empty.");

                }
            }
        });
    }
}
