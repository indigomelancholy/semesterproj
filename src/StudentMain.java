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
import java.sql.SQLException;
import java.util.List;

public class StudentMain extends JFrame {
    private JButton gradesManagementButton;
    private JButton enrolInCourseButton;
    private JLabel nameLabel;
    private JPanel panel;
    private JLabel photolabel;

    public StudentMain() {
        setSize(800, 600);
        setContentPane(panel);

        panel.setBackground(new Color(241, 182, 238));

        Font font = new Font("Roboto", Font.PLAIN, 20);
        Font buttonFont = new Font("Cambria", Font.BOLD, 24);
        Font labelFont = new Font("Roboto", Font.BOLD, 18);

        nameLabel.setFont(labelFont);
        nameLabel.setForeground(new Color(115, 76, 187));
        nameLabel.setText("Logged in as: " + User.getFirstName() + " " + User.getLastName());

        gradesManagementButton.setBackground(new Color(115, 76, 187));
        gradesManagementButton.setForeground(Color.WHITE);
        gradesManagementButton.setFont(buttonFont);
        gradesManagementButton.setPreferredSize(new Dimension(250, 40));

        enrolInCourseButton.setBackground(new Color(115, 76, 187));
        enrolInCourseButton.setForeground(Color.WHITE);
        enrolInCourseButton.setFont(buttonFont);
        enrolInCourseButton.setPreferredSize(new Dimension(250, 40));


        String firstName = User.getFirstName();
        String lastName = User.getLastName();
        InputStream imgStream = User.getPhotoStream();

        if (imgStream != null) {
            try {
                BufferedImage userImage = ImageIO.read(imgStream);
                Image scaledImg = userImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                photolabel.setIcon(new ImageIcon(scaledImg));

            } catch (IOException e) {
                System.out.println("Error loading user photo: " + e.getMessage());
                photolabel.setText("No photo");
            }
        } else {
            photolabel.setText("No photo");
        }

        setVisible(true);


        gradesManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int studentID = User.getStudentID();

                new GradeManagement(studentID);

            }
        });

        enrolInCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                List<Course> courses = connect.getCourses();
               JComboBox<Course> courseOptions = new JComboBox<>();

               if (courses.isEmpty()){
                   JOptionPane.showMessageDialog(StudentMain.this,"No courses available");
                   return;
               }

                for (Course course : courses) {
                    courseOptions.addItem(course);
                }


               int result = JOptionPane.showConfirmDialog(
                       StudentMain.this, courseOptions,
                       "Enroll in course? ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

               if (result == JOptionPane.OK_OPTION){
                   Course selected = (Course) courseOptions.getSelectedItem();

                   int studentID = User.getStudentID();

                   try(Connection connection = connect.getConnection()) {
                       PreparedStatement check = connection.prepareStatement("SELECT  * FROM Grades where StudentID = ? and CourseID = ?");

                       check.setInt(1, studentID);
                       check.setInt(2, selected.id);

                       ResultSet rs = check.executeQuery();

                       if (rs.next()) {
                           JOptionPane.showMessageDialog(StudentMain.this,
                                   "You have already enrolled in this course!");


                       } else {
                           PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Grades (StudentID, CourseID) values (?, ?)");
                           pstmt.setInt(1, studentID);
                           pstmt.setInt(2, selected.id);

                           pstmt.executeUpdate();

                           JOptionPane.showMessageDialog(StudentMain.this,
                                   "Enrolled in: " + selected.name);
                       }

                   }catch (SQLException ex){
                           if (ex.getMessage().contains("foreign key constraint")) {
                               JOptionPane.showMessageDialog(StudentMain.this,
                                       "Error: Invalid student or course ID");
                           } else {
                               JOptionPane.showMessageDialog(StudentMain.this,
                                       "Error: " + ex.getMessage());
                           }
                       }
                   }
               }
        });
    }
}
