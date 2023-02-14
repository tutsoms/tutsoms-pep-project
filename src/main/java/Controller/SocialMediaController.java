package Controller;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Util.ConnectionUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;
/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    
    private AccountService accountService;

    public SocialMediaController() {
        this.accountService = new AccountService();
    }

    public static class AuthUtil {
        private static final String HASH_ALGORITHM = "SHA-256";
    
        public static String hashPassword(String password) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
                byte[] hash = messageDigest.digest(password.getBytes());
                StringBuilder stringBuilder = new StringBuilder();
                for (byte b : hash) {
                    stringBuilder.append(String.format("%02x", b));
                }
                return stringBuilder.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Could not hash password", e);
            }
        }
    
        public static boolean checkPassword(String password, String hashedPassword) {
            return hashPassword(password).equals(hashedPassword);
        }
    }
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create().start(8080);
        app.post("/register", this::register);
        app.post("/login", this::login);
        app.post("/messages", this::createMessage);

        return app;
    }

    /**
     * This endpoint allows the user to register for the social media platform.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void register(Context context) throws IOException, SQLException {
        Account account = context.bodyAsClass(Account.class);

        if (account.getUsername().isBlank()) {
            context.status(400);
            context.result("Invalid account information");
            return;
        }

        if (account.getPassword().length() < 4) {
            context.status(400);
            context.result("Invalid account information");
            return;
        }

        Account existingAccount = accountService.findByUsername(account.getUsername());
        if (existingAccount != null) {
            context.status(400);
            context.result("Account already exists");
            return;
        }

        account.setPassword(AuthUtil.hashPassword(account.getPassword()));
        int id = accountService.createAccount(account.getUsername(), account.getPassword());
        Account responseAccount = new Account(id, account.getUsername(), account.getPassword());
        context.status(200);
        context.json(responseAccount);
    }



    /**
     * This endpoint allows the user to log in to the social media platform.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void login(Context context) throws IOException, SQLException {
        Account account = context.bodyAsClass(Account.class);
    
        Account existingAccount = accountService.findByUsername(account.getUsername());
        if (existingAccount == null || !AuthUtil.checkPassword(account.getPassword(), existingAccount.getPassword())) {
                context.status(401);
                context.result("Invalid credentials");
                return;
            }
    
            context.status(200);
            context.json(existingAccount);
        }
        
        /**
         * This endpoint allows the user to post a message to their profile.
         * @param context The Javalin Context object manages information about both the HTTP request and response.
         */
        private void createMessage(Context context) throws IOException, SQLException {
            Account account = context.bodyAsClass(Account.class);
            Account existingAccount = accountService.findByUsername(account.getUsername());
            if (existingAccount == null || !AuthUtil.checkPassword(account.getPassword(), existingAccount.getPassword())) {
                context.status(401);
                context.result("Invalid credentials");
                return;
            }
        
            Message message = context.bodyAsClass(Message.class);
            if (message.getMessage_id() == 0) {
                context.status(400);
                context.result("Invalid message id");
                return;
            }
        
            int messageId = accountService.createMessage(message.getMessage_id(), message.getMessage_text(), message.getTime_posted_epoch());
            context.status(200);
            context.result("Successfully created message with id: " + messageId);
        }
}
        