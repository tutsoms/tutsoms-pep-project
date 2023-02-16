package Controller;

import java.sql.SQLException;
import java.util.List;

import Model.Account;
import Model.Message;
import Service.SocialMediaService;
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

    
    private SocialMediaService socialMediaService;
    
    
 
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        this.socialMediaService = new SocialMediaService(null);
        app.post("/register", this::createAccount);
        app.post("/login", this::login);
        app.post("/messages", this::createMessage);
        app.get("/messages", this::getAllMessages);
        app.get("/messages/{id}", this::getMessageById);
        app.delete("/messages/{id}", this::deleteMessageById);
        app.put("/messages/{id}", this::updateMessageById);
        app.get("/accounts/{id}/messages", this::getAllMessagesByAccount);


        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */

     private void createAccount(Context ctx) throws SQLException {
        Account account = ctx.bodyAsClass(Account.class);
        Account createdAccount = socialMediaService.createAccount(account);
        ctx.status(201).json(createdAccount);
    }

    private void login(Context ctx) throws SQLException {
        Account account = ctx.bodyAsClass(Account.class);
        Account loggedInAccount = socialMediaService.login(account);
        if (loggedInAccount == null) {
            ctx.status(401);
        } else {
            ctx.status(200).json(loggedInAccount);
        }
    }

    private void createMessage(Context ctx) throws SQLException {
        Message message = ctx.bodyAsClass(Message.class);
        if (message.getMessage_text().isBlank() || message.getMessage_text().length() > 255
                || !socialMediaService.isAccountExist(message.getPosted_by())) {
            ctx.status(400).result("Invalid message data.");
            return;
        }
        Message createdMessage = socialMediaService.createMessage(message);
        ctx.status(201).json(createdMessage);
    }

    private void getAllMessages(Context ctx) throws SQLException {
        List<Message> messages = socialMediaService.getAllMessages();
        ctx.json(messages);
    }

    private void getMessageById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Message message = socialMediaService.getMessageById(id);
        if (message == null) {
            ctx.status(404);
        } else {
            ctx.json(message);
        }
    }

    private void deleteMessageById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Message deletedMessage = socialMediaService.deleteMessageById(id);
        if (deletedMessage == null) {
            ctx.status(404);
        } else {
            ctx.json(deletedMessage);
        }
    }

    private void updateMessageById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Message message = ctx.bodyAsClass(Message.class);
        if (message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
            ctx.status(400);
        } else {
            Message updatedMessage = socialMediaService.updateMessageById(id, message);
            if (updatedMessage == null) {
                ctx.status(404);
            } else {
                ctx.json(updatedMessage);
            }
        }
    }

    private void getAllMessagesByAccount(Context ctx) throws SQLException {
        int accountId = Integer.parseInt(ctx.pathParam("id"));
        List<Message> messages = socialMediaService.getAllMessagesByAccount(accountId);
        ctx.json(messages);
    }
}