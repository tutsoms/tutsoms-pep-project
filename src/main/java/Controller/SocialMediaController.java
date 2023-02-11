package Controller;

import java.io.IOException;
import java.sql.SQLException;

import Model.Account;
import Util.ConnectionUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("/register", this::register);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void register(Context context) throws IOException, SQLException {
        Account account = ctx.bodyAsClass(Account.class);

        if (account.getUsername().isBlank()) {
            ctx.status(400);
            return;
        }

        if (account.getPassword().length() < 4) {
            ctx.status(400);
            return;
        }

        account.setPassword(AuthUtil.hashPassword(account.getPassword()));
        int id = ConnectionUtil.getAccountDAO().insert(account);
        account.setId(id);
        ctx.status(200);
        ctx.json(account);
    }
}