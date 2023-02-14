package Service;

import DAO.AccountDAO;
import Model.Account;
import Model.Message;

import java.sql.SQLException;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    public Account findByUsername(String username) throws SQLException {
        return accountDAO.findByUsername(username);
    }

    public int createAccount(String username, String password) throws SQLException {
        Account account = new Account(username, password);
        return accountDAO.insert(account);
    }

    public int createMessage(int message_id, String message_text, long time_posted_epoch) {
       
        new Message(message_id, message_text, time_posted_epoch);
        return 0;
    }

}
