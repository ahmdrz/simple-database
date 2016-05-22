public class Main {
    public static void main(String[] args) {
        try (
                StudentDatabase db = new StudentDatabase("object.obj")
        ){
            db.insertStudent(new Student(1,20.0F,"A"));
            db.insertStudent(new Student(1,20.1F,"A"));
            db.insertStudent(new Student(2,20.0F,"A"));
            db.insertStudent(new Student(3,20.0F,"B"));
            db.insertStudent(new Student(3,20.0F,"A"));

            for (int i = 0;i < db.length();i++)
                System.out.println(db.getStudent(i));

        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }
}
