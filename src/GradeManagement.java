import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class GradeManagement extends JFrame{
    private JTable table1;
    private JTextField searchbar;
    private JButton backButton;
    private JButton calculateGPAButton;
    private JPanel panel;
    private DefaultTableModel model;
    private JLabel gpaLabel;

    private int studentID;

    public GradeManagement(int studentID) {
        this.studentID = studentID;

        setSize(500, 500);
        setContentPane(panel);
        setVisible(true);

        model = new DefaultTableModel(new String[]{"Course name", "Grade", "Teacher name"}, 0);

        table1.setModel(model);

        loadGrades();

        searchbar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {

                String searchText = searchbar.getText().trim().toLowerCase();
                filterGrades(searchText);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                dispose();
                new StudentMain();
            }
        });

        calculateGPAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                double gpa = calculateGPA();
                gpaLabel.setText("Your GPA: " + String.format("%.2f", gpa));
            }
        });

    }

    private void loadGrades() {
        try (Connection conn = connect.getConnection()) {
            String query = "SELECT c.CourseName, g.Grade, t.TeacherName FROM Grades g " +
                    "JOIN Courses c ON g.CourseID = c.CourseID " +
                    "JOIN Teachers t ON c.TeacherID = t.TeacherID " +
                    "WHERE g.StudentID = ?";


            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, studentID);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {

                    String courseName = rs.getString("CourseName");
                    double grade = rs.getDouble("Grade");
                    String teacherName = rs.getString("TeacherName");

                    model.addRow(new Object[]{courseName, grade, teacherName});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void filterGrades(String searchText) {
        DefaultTableModel filteredModel = new DefaultTableModel(new String[]{"Course Name", "Grade", "Teacher Name"}, 0);
        for (int row = 0; row < model.getRowCount(); row++) {

            String courseName = model.getValueAt(row, 0).toString().toLowerCase();

            String grade = model.getValueAt(row, 1).toString().toLowerCase();

            String teacherName = model.getValueAt(row, 2).toString().toLowerCase();


            if (courseName.contains(searchText) || grade.contains(searchText) || teacherName.contains(searchText)) {

                filteredModel.addRow(new Object[]{model.getValueAt(row, 0), model.getValueAt(row, 1), model.getValueAt(row, 2)});
            }
        }
        table1.setModel(filteredModel);
    }

    private double calculateGPA() {
        double totalPoints = 0;
        int totalCourses = 0;

        for (int row = 0; row < model.getRowCount(); row++) {

            double grade = (Double) model.getValueAt(row, 1);


            totalPoints += grade;
            totalCourses++;
        }

        return (totalCourses == 0) ? 0 : totalPoints / totalCourses;
    }


}
