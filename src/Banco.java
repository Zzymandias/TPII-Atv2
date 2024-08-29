import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Banco {
    public static Connection connect() throws SQLException {
        try {
            var jdbcUrl = "jdbc:postgresql://localhost:5432/postgres";
            var user = "post";
            var password = "123";
            return DriverManager.getConnection(jdbcUrl, user, password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }
}
