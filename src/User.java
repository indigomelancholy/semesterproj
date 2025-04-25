public class User {
    private static int studentID;
    private static int teacherID;
    private static String firstName;
    private static String lastName;

    public static void setStudentID(int id) {
        studentID = id;
    }

    public static int getStudentID() {
        return studentID;
    }

    public static void setTeacherID(int id) {
        teacherID = id;
    }

    public static int getTeacherID() {
        return teacherID;
    }

    public static void setFirstName(String name) {
        firstName = name;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static void setLastName(String name) {
        lastName = name;
    }

    public static String getLastName() {
        return lastName;
    }
}
