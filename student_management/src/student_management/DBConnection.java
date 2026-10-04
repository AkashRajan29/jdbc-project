package student_management;
import java.sql.*;

public class DBConnection {

    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/student_db",
            "root",
            "admin"
        );
    }
}