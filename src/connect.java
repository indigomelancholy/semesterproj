import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;


public class connect {
    private static final String URL = "jdbc:mysql://localhost:3306/academicdb";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    public static String[] loginMethod(String email, String password, String role) {
        String[] userInfo = null;
        String query = null;

        if (role.equalsIgnoreCase("student")) {
            query = "SELECT StudentID, FirstName, LastName, Password FROM Students WHERE Email = ?";
        } else if (role.equalsIgnoreCase("teacher")) {
            query = "SELECT TeacherID, FirstName, LastName, Password FROM Teachers WHERE Email = ?";
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

                        userInfo = new String[]{id, firstName, lastName};
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




}


