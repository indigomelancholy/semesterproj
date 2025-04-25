import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class StudentMain extends JFrame {
    private JButton gradesManagementButton;
    private JButton enrolInCourseButton;
    private JLabel nameLabel;
    private JPanel panel;

    public StudentMain() {
        setSize(400, 400);
        setContentPane(panel);
        setVisible(true);

        nameLabel.setText("Logged in as: " + User.getFirstName() + " " + User.getLastName());


        gradesManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new CourseManagement();

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
                       "Enroll in course? ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
                              );

               if (result == JOptionPane.OK_OPTION){
                   Course selected = (Course) courseOptions.getSelectedItem();

                   int studentID = User.getStudentID();

                   try(Connection connection = connect.getConnection();
                       PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Grades (StudentID, CourseID) VALUES (?, ?)")){

                       pstmt.setInt(1, studentID);
                       pstmt.setInt(2, selected.id);
                       pstmt.executeUpdate();

                       JOptionPane.showMessageDialog(StudentMain.this,
                               "Enrolled in: " + selected.name);
                   } catch (SQLException ex) {
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
