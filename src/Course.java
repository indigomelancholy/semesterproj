public class Course{
    public final int id;
    public final String name;

    public Course(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getID() {
        return id; }
    public String getName() {
        return name; }

    @Override
    public String toString() {
        return name + " (ID: " + id +")";
    }
}