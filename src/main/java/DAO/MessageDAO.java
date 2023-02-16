package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Model.Message;

public class MessageDAO {

    private final Connection connection;

    public MessageDAO(Connection connection) {
        this.connection = connection;
    }

    public Message insert(Message message) throws SQLException {
        String query = "INSERT INTO messages (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, message.getPosted_by());
            statement.setString(2, message.getMessage_text());
            statement.setLong(3, message.getTime_posted_epoch());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    message.setMessage_id(id);
                    return message;
                } else {
                    throw new SQLException("Creating message failed, no ID obtained.");
                }
            }
        }
    }

    public List<Message> findAll() throws SQLException {
        String query = "SELECT * FROM messages";
        try (PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {
            List<Message> messages = new ArrayList<>();
            while (resultSet.next()) {
                messages.add(createMessage(resultSet));
            }
            return messages;
        }
    }

    public Message findById(int id) throws SQLException {
        String query = "SELECT * FROM messages WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return createMessage(resultSet);
                } else {
                    return null;
                }
            }
        }
    }

    public Message deleteById(int id) throws SQLException {
        Message message = findById(id);
        if (message == null) {
            return null;
        }
        String query = "DELETE FROM messages WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
        return message;
    }

    public void update(Message message) throws SQLException {
        String query = "UPDATE messages SET message_text = ?, time_posted_epoch = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, message.getMessage_text());
            statement.setLong(2, message.getTime_posted_epoch());
            statement.setInt(3, message.getMessage_id());
            statement.executeUpdate();
        }
    }

    private Message createMessage(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int posted_by = resultSet.getInt("posted_by");
        String message_text = resultSet.getString("message_text");
        long time_posted_epoch = resultSet.getLong("time_posted_epoch");

        return new Message(id, posted_by, message_text, time_posted_epoch);
    }

    public Message updateById(int id, Message message) throws SQLException {
        String query = "UPDATE messages SET message_text = ?, time_posted_epoch = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, message.getMessage_text());
            statement.setLong(2, message.getTime_posted_epoch());
            statement.setInt(3, id);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                return null;
            }
            message.setMessage_id(id);
            return message;
        }
    }
    

    public List<Message> findAllByAccount(int accountId) throws SQLException {
        String query = "SELECT * FROM messages WHERE posted_by = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Message> messages = new ArrayList<>();
                while (resultSet.next()) {
                    messages.add(createMessage(resultSet));
                }
                return messages;
            }
        }
    }
    
}
