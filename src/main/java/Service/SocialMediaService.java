package Service;

import DAO.SocialMediaDAO;
import Model.Account;
import Model.Message;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SocialMediaService {
    private static SocialMediaDAO socialMediaDAO;

    public SocialMediaService(Connection connection) {
        socialMediaDAO = new SocialMediaDAO(connection);
    }

    public static Account createAccount(Account account) throws SQLException {
        return socialMediaDAO.createAccount(account);
    }

    public Account login(Account account) throws SQLException {
        return socialMediaDAO.login(account);
    }

    public Message createMessage(Message message) throws SQLException {
        return socialMediaDAO.createMessage(message);
    }

    public List<Message> getAllMessages() throws SQLException {
        return socialMediaDAO.getAllMessages();
    }

    public Message getMessageById(int messageId) throws SQLException {
        return socialMediaDAO.getMessageById(messageId);
    }

    public Message deleteMessage(int messageId) throws SQLException {
        return socialMediaDAO.deleteMessage(messageId);
    }

    public Message deleteMessageById(int messageId) throws SQLException {
        return socialMediaDAO.deleteMessageById(messageId);
    }

    public boolean isAccountExist(String username) {
        return false;
    }

    public boolean isAccountExist(int posted_by) {
        return false;
    }

    public List<Message> getAllMessagesByAccount(int accountId) {
        return null;
    }

    public Message updateMessageById(int id, Message message) {
        return null;
    }
}
