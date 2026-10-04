package student_management;

import java.sql.*;
import java.util.Scanner;

public class StudentManagement {

    static Scanner sc = new Scanner(System.in);

    static void addStudent() {

        String sql =
            "INSERT INTO students(name,email,phone) " +
            "VALUES(?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Email: ");
            String email = sc.nextLine();

            System.out.print("Enter Phone: ");
            String phone = sc.nextLine();

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);

            ps.executeUpdate();

            System.out.println(
                "Student added successfully!");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    static void viewStudents() {

        String sql =
            "SELECT * FROM students";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println(
                "\nID\tName\tEmail\tPhone");

            while (rs.next()) {

                System.out.println(
                    rs.getInt("student_id") + "\t" +
                    rs.getString("name") + "\t" +
                    rs.getString("email") + "\t" +
                    rs.getString("phone"));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    static void updateStudent() {

        String sql =
            "UPDATE students SET email=? WHERE student_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                 con.prepareStatement(sql)) {

            System.out.print("Enter Student ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter New Email: ");
            String email = sc.nextLine();

            ps.setString(1, email);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println(
                    "Student updated successfully!");
            else
                System.out.println("Student not found.");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    static void deleteStudent() {

        String sql =
            "DELETE FROM students WHERE student_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                 con.prepareStatement(sql)) {

            System.out.print("Enter Student ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println(
                    "Student deleted successfully!");
            else
                System.out.println("Student not found.");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    static void showResults() {

        String sql =
            "SELECT s.name, c.course_name, m.marks " +
            "FROM students s " +
            "JOIN marks m ON s.student_id=m.student_id " +
            "JOIN courses c ON m.course_id=c.course_id";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println(
                "\nStudent\tCourse\tMarks");

            while (rs.next()) {

                System.out.println(
                    rs.getString("name") + "\t" +
                    rs.getString("course_name") + "\t" +
                    rs.getInt("marks"));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public static void main(String[] args) {

        while (true) {

            System.out.println(
                "\n===== STUDENT MANAGEMENT SYSTEM =====");

            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Update Student");
            System.out.println("4. Delete Student");
            System.out.println("5. View Results");
            System.out.println("6. Exit");

            System.out.print("Enter Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    addStudent();
                    break;

                case 2:
                    viewStudents();
                    break;

                case 3:
                    updateStudent();
                    break;

                case 4:
                    deleteStudent();
                    break;

                case 5:
                    showResults();
                    break;

                case 6:
                    System.out.println("Thank You!");
                    System.exit(0);

                default:
                    System.out.println("Invalid Choice");
            }
        }
    }
}