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

                if (gpa >= 50.00) {
                    gpaLabel.setForeground(Color.GREEN);
                } else if (gpa >= 40.00) {
                    gpaLabel.setForeground(Color.ORANGE);
                } else {
                    gpaLabel.setForeground(Color.RED);
                }

                gpaLabel.setText("Your GPA: " + String.format("%.2f", gpa));
            }
        });

    }

    private void loadGrades() {
        try (Connection conn = connect.getConnection()) {
            String query = "SELECT c.CourseName, g.Grade, CONCAT(t.FirstName, ' ', t.LastName) AS TeacherName " +
                    "FROM Grades g " +
                    "JOIN Courses c ON g.CourseID = c.CourseID " +
                    "JOIN Teachers t ON c.TeacherID = t.TeacherID " +
                    "WHERE g.StudentID = ?";



            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, studentID);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {

                    String courseName = rs.getString("CourseName");
                    String Grade = rs.getString("Grade");
                    String teacherName = rs.getString("TeacherName");

                    model.addRow(new Object[]{courseName, Grade, teacherName});
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
            Object gradeObj = model.getValueAt(row, 1);
            String grade = model.getValueAt(row, 1).toString();

            if (gradeObj != null) {
                try {
                    double points = Double.parseDouble(gradeObj.toString());

                    totalPoints += points;
                    totalCourses++;

                } catch (NumberFormatException e) {
                    System.out.println("Invalid grade found, skipping row " + row);

                }
            }
        }

        return (totalCourses == 0) ? 0 : totalPoints / totalCourses;
    }


}
