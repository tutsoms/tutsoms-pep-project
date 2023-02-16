package Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Account;
import Model.Message;

public class SocialMediaService {

    private final AccountDAO accountDAO;
    private final MessageDAO messageDAO;

    public SocialMediaService(Connection connection) {
        this.accountDAO = new AccountDAO(connection);
        this.messageDAO = new MessageDAO(connection);
    }

    public Account createAccount(String username, String password) throws SQLException {
        Account account = new Account(0, username, password);
        return accountDAO.insert(account);
    }

    public Account getAccountById(int accountId) throws SQLException {
        return accountDAO.findById(accountId);
    }

    public Account getAccountByUsername(String username) throws SQLException {
        return accountDAO.findByUsername(username);
    }

    public Message createMessage(int accountId, String messageText, long timePostedEpoch) throws SQLException {
        Account account = accountDAO.findById(accountId);
        if (account == null) {
            throw new SQLException("Account does not exist.");
        }
        Message message = new Message(0, accountId, messageText, timePostedEpoch);
        return messageDAO.insert(message);
    }

    public List<Message> getAllMessages() throws SQLException {
        return messageDAO.findAll();
    }

    public Message getMessageById(int messageId) throws SQLException {
        return messageDAO.findById(messageId);
    }

    public Message deleteMessageById(int messageId) throws SQLException {
        return messageDAO.deleteById(messageId);
    }

    public void updateMessage(Message message) throws SQLException {
        messageDAO.update(message);
    }

    public Account createAccount(Account account) throws SQLException {
        return accountDAO.insert(account);
    }

    public Account login(Account account) throws SQLException {
        return accountDAO.login(account);
    }

    public boolean isAccountExist(int posted_by) throws SQLException {
        return accountDAO.isAccountExist(posted_by);
    }

    public Message createMessage(Message message) throws SQLException {
        return messageDAO.insert(message);
    }

    public Message updateMessageById(int id, Message message) throws SQLException {
        return messageDAO.updateById(id, message);
    }

    public List<Message> getAllMessagesByAccount(int accountId) throws SQLException {
        return messageDAO.findAllByAccount(accountId);
    }
}
