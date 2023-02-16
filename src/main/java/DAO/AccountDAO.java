package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Model.Account;

public class AccountDAO {

    private final Connection connection;

    public AccountDAO(Connection connection) {
        this.connection = connection;
    }

    public Account insert(Account account) throws SQLException {
        String sql = "INSERT INTO accounts(username, password) VALUES(?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, account.getUsername());
            statement.setString(2, account.getPassword());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating account failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    account.setAccount_id((generatedKeys.getInt(1)));
                } else {
                    throw new SQLException("Creating account failed, no ID obtained.");
                }
            }
        }
        return account;
    }

    public Account findById(int id) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return createAccountFromResultSet(resultSet);
                }
            }
        }
        return null;
    }

    public Account findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return createAccountFromResultSet(resultSet);
                }
            }
        }
        return null;
    }

    private Account createAccountFromResultSet(ResultSet resultSet) throws SQLException {
        int account_id = resultSet.getInt("account_id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        return new Account(account_id, username, password);
    }

    public Account login(Account account) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, account.getUsername());
            statement.setString(2, account.getPassword());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return createAccountFromResultSet(resultSet);
                }
            }
        }
        return null;
    }
    
    public boolean isAccountExist(int posted_by) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM accounts WHERE account_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, posted_by);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count > 0;
                }
            }
        }
        return false;
    }
}
