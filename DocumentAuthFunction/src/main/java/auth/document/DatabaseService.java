package auth.document;

import java.sql.*;

public class DatabaseService implements CustomerStatusService  {

    private Connection getConnection() throws SQLException {
        String url = String.format(
                "jdbc:postgresql://%s:%s/%s?currentSchema=%s&ssl=false",
                System.getenv("DB_HOST"),
                System.getenv("DB_PORT"),
                System.getenv("DB_NAME"),
                System.getenv("DB_SCHEMA")
        );
        return DriverManager.getConnection(url, System.getenv("DB_USER"), System.getenv("DB_PASSWORD"));
    }

    @Override
    public boolean isActiveCustomer(String documentNumber) {
        String sql = "SELECT is_active FROM customers WHERE document_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, documentNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_active");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao acessar a base de dados", e);
        }
        return false;
    }
}
