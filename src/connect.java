import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.InputStream;



public class connect {
    private static final String URL = "jdbc:mysql://localhost:3306/academicdb";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    public static Object[] loginMethod(String email, String password, String role) {
        Object[] userInfo = null;
        String query = null;

        if (role.equalsIgnoreCase("student")) {
            query = "SELECT StudentID, FirstName, LastName, Password, Photo FROM Students WHERE Email = ?";
        } else if (role.equalsIgnoreCase("teacher")) {
            query = "SELECT TeacherID, FirstName, LastName, Password, Photo FROM Teachers WHERE Email = ?";
        }

        if (query == null) return null;

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("Password");

                    if (BCrypt.checkpw(password, hashedPassword)) {
                        String id = role.equalsIgnoreCase("student") ? rs.getString("StudentID") : rs.getString("TeacherID");
                        String firstName = rs.getString("FirstName");
                        String lastName = rs.getString("LastName");
                        InputStream imgStream = rs.getBinaryStream("Photo");

                        userInfo = new Object[]{id, firstName, lastName, imgStream};
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return userInfo;
    }

    public static List<Course> getCourses(){
        List<Course> courses = new ArrayList<>();

        String query = "SELECT CourseID, CourseName FROM Courses";

        try(Connection connection = getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query)){

            while (rs.next()){
                courses.add(new Course(
                        rs.getInt("CourseID"),
                        rs.getString("CourseName")
                ));
            }
        }catch(SQLException e){
            System.out.println("Error retrieving courses: " + e.getMessage());
        }
        return courses;
    }

///

    public static ArrayList<String[]> getAllStudentsWithGPA() {
        ArrayList<String[]> students = new ArrayList<>();

        String query = "SELECT s.StudentID, s.FirstName, s.LastName, s.Photo, s.Age, s.Email, " +
                "COALESCE(ROUND(AVG(g.Grade),2), 0) AS GPA " +
                "FROM Students s LEFT JOIN Grades g ON s.StudentID = g.StudentID " +
                "GROUP BY s.StudentID";

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String[] student = new String[7];
                student[0] = rs.getString("StudentID");
                student[1] = rs.getString("FirstName");
                student[2] = rs.getString("LastName");
                student[3] = rs.getString("Photo");
                student[4] = rs.getString("Age");
                student[5] = rs.getString("Email");
                student[6] = rs.getString("GPA");
                students.add(student);
            }
        } catch (SQLException e) {
            System.out.println("Error getting student data: " + e.getMessage());
        }

        return students;
    }

    public static void updateStudentField(String studentID, String columnName, String newValue) {
        String query = "UPDATE Students SET " + columnName + " = ? WHERE StudentID = ?";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, newValue);
            pstmt.setString(2, studentID);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }

    public static void deleteStudent(String studentID) {
        String query = "DELETE FROM Students WHERE StudentID = ?";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, studentID);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error deleting student: " + e.getMessage());
        }
    }

    public static void addStudentWithFields(String studentID, String firstName, String lastName, int age, String email, String password, InputStream photo) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String query = "INSERT INTO Students (StudentID, FirstName, LastName, Age, Email, Password, Photo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, studentID);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setInt(4, age);
            pstmt.setString(5, email);
            pstmt.setString(6, hashedPassword);
            pstmt.setBlob(7, photo);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding student with fields: " + e.getMessage());
        }
    }


    public static ArrayList<Object[]> getAllStudentPhotos() {
        ArrayList<Object[]> photos = new ArrayList<>();
        String query = "SELECT StudentID, Photo FROM Students WHERE Photo IS NOT NULL";

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String studentID = rs.getString("StudentID");
                Blob photoBlob = rs.getBlob("Photo");
                photos.add(new Object[]{studentID, photoBlob});
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving student photos: " + e.getMessage());
        }
        return photos;
    }
}


