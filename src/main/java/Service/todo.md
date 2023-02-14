package Service;

import java.io.IOException;
import java.sql.SQLException;

import Model.Account;
import Util.AuthUtil;
import Util.ConnectionUtil;

public class AccountService {

    public Account register(Account account) throws IOException, SQLException {
        if (account.getUsername().isBlank()) {
            throw new IOException("Invalid account information");
        }

        if (account.getPassword().length() < 4) {
            throw new IOException("Invalid account information");
        }

        Account existingAccount = ConnectionUtil.getAccountDAO().findByUsername(account.getUsername());
        if (existingAccount != null) {
            throw new IOException("Account already exists");
        }

        account.setPassword(AuthUtil.hashPassword(account.getPassword()));
        int id = ConnectionUtil.getAccountDAO().insert(account);
        account.setId(id);

        return account;
    }

    public Account login(String username, String password) throws IOException, SQLException {
        Account existingAccount = ConnectionUtil.getAccountDAO().findByUsername(username);
        if (existingAccount == null || !AuthUtil.checkPassword(password, existingAccount.getPassword())) {
            throw new IOException("Invalid username or password");
        }

        return existingAccount;
    }
}
