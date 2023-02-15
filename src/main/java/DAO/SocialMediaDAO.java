package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Model.Account;
import Model.Message;

public class SocialMediaDAO {
    private Connection connection;

    public SocialMediaDAO(Connection connection) {
        this.connection = connection;
    }

    // User Registration
    public Account createAccount(Account account) throws SQLException {
        if (account.getUsername().isBlank() || account.getPassword().length() < 4) {
            throw new IllegalArgumentException("Username must not be blank and password must be at least 4 characters long.");
        }

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM accounts WHERE username = ?")) {
            statement.setString(1, account.getUsername());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                throw new IllegalArgumentException("An account with that username already exists.");
            }
        }

        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO accounts (username, password) VALUES (?, ?)")) {
            statement.setString(1, account.getUsername());
            statement.setString(2, account.getPassword());
            statement.executeUpdate();
        }

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM accounts WHERE username = ?")) {
            statement.setString(1, account.getUsername());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int accountId = resultSet.getInt("account_id");
                return new Account(accountId, account.getUsername(), account.getPassword());
            }
        }

        throw new SQLException("Unable to create account.");
    }

    // Login
    public Account login(Account account) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM accounts WHERE username = ? AND password = ?")) {
            statement.setString(1, account.getUsername());
            statement.setString(2, account.getPassword());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int accountId = resultSet.getInt("account_id");
                return new Account(accountId, account.getUsername(), account.getPassword());
            }
        }

        throw new IllegalArgumentException("Invalid username or password.");
    }

    // Create New Message
    public Message createMessage(Message message) throws SQLException {
        if (message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
            throw new IllegalArgumentException("Message text must not be blank and must be 255 characters or less.");
        }

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM accounts WHERE posted_by = ?")) {
            statement.setInt(1, message.getPosted_by());
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new IllegalArgumentException("Posted by user does not exist.");
            }
        }

        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO messages (message_text, posted_by) VALUES (?, ?)")) {
            statement.setString(1, message.getMessage_text());
            statement.setInt(2, message.getPosted_by());
            statement.executeUpdate();
        }

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE message_text = ? AND posted_by = ?")) {
            statement.setString(1, message.getMessage_text());
            statement.setInt(2, message.getPosted_by());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int messageId = resultSet.getInt("message_id");
                return new Message(messageId, message.getMessage_text(), message.getPosted_by());
            }
        }

        throw new SQLException("Unable to create message.");
    }


    // Get All Messages
public List<Message> getAllMessages() throws SQLException {
    List<Message> messages = new ArrayList<>();

    try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages ORDER BY time_posted_epoch DESC")) {
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            int messageId = resultSet.getInt("message_id");
            int postedByAccountId = resultSet.getInt("posted_by");
            long timePostedEpoch = resultSet.getLong("time_posted_epoch");
            String messageText = resultSet.getString("message_text");

            try (PreparedStatement statement2 = connection.prepareStatement("SELECT * FROM accounts WHERE account_id = ?")) {
                statement2.setInt(1, postedByAccountId);
                ResultSet resultSet2 = statement2.executeQuery();
                if (resultSet2.next()) {
                    int accountId = resultSet2.getInt("account_id");
                    String username = resultSet2.getString("username");
                    String password = resultSet2.getString("password");

                    Account account = new Account(accountId, username, password);
                    Message message = new Message(messageId, messageText, timePostedEpoch);
                    messages.add(message);
                }
            }
        }
    }

    return messages;
}
// Get One Message Given Message Id
public Message getMessageById(int messageId) throws SQLException {
    try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE message_id = ?")) {
        statement.setInt(1, messageId);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            int postedByAccountId = resultSet.getInt("posted_by");
            long timePostedEpoch = resultSet.getLong("time_posted_epoch");
            String messageText = resultSet.getString("message_text");

            try (PreparedStatement statement2 = connection.prepareStatement("SELECT * FROM accounts WHERE account_id = ?")) {
                statement2.setInt(1, postedByAccountId);
                ResultSet resultSet2 = statement2.executeQuery();
                if (resultSet2.next()) {
                    int accountId = resultSet2.getInt("account_id");
                    String username = resultSet2.getString("username");
                    String password = resultSet2.getString("password");

                    Account account = new Account(accountId, username, password);
                    Message message = new Message(messageId, messageText, timePostedEpoch);
                    return message;
                }
            }
        }
    }
    return null;
}

// Delete Message Given Message Id
public Message deleteMessage(int messageId) throws SQLException {
    Message message = getMessageById(messageId);

    if (message != null) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM messages WHERE message_id = ?")) {
            statement.setInt(1, messageId);
            statement.executeUpdate();
        }

        return message;
    }

    return null;
}

// Update Message Given Message Id
public Message updateMessage(int messageId, Message message) throws SQLException {
    if (message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
        throw new IllegalArgumentException("Message text must not be blank and must be 255 characters or less.");
    }

    Message existingMessage = getMessageById(messageId);

    if (existingMessage != null) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE messages SET message_text = ? WHERE message_id = ?")) {
            statement.setString(1, message.getMessage_text());
            statement.setInt(2, messageId);
            statement.executeUpdate();
        }

        return getMessageById(messageId);
    }

    return null;
}

public Message deleteMessageById(int messageId) {
    return null;
}
}