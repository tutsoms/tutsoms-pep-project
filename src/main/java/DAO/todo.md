import java.sql.*;

public class AccountDAO {
    private Connection connection;

    public AccountDAO(Connection connection) {
        this.connection = connection;
    }

    public int registerAccount(Account account) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO accounts (username, password) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, account.getUsername());
        stmt.setString(2, account.getPassword());
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        rs.next();
        return rs.getInt(1);
    }

    public Account getAccountByUsername(String username) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM accounts WHERE username = ?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            return null;
        }
        int id = rs.getInt("id");
        String returnedUsername = rs.getString("username");
        String password = rs.getString("password");
        return new Account(id, returnedUsername, password);
    }
}
