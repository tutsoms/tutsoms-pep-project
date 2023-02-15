package Controller;

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

     private SocialMediaService service;

     public SocialMediaController() {
         this.service = service;
     }
 
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/accounts", this::createAccount);
        app.post("/login", this::login);

        app.get("/messages", this::getAllMessages);
        app.post("/messages", this::createMessage);
        app.get("/messages/:id", this::getMessageById);
        app.delete("/messages/:id", this::deleteMessageById);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void createAccount(Context ctx) throws Exception {
        Account account = ctx.bodyAsClass(Account.class);
        Account createdAccount = SocialMediaService.createAccount(account);
        ctx.json(createdAccount);
    }

    private void login(Context ctx) throws Exception {
        Account account = ctx.bodyAsClass(Account.class);
        Account loggedInAccount = service.login(account);
        ctx.json(loggedInAccount);
    }

    private void createMessage(Context ctx) throws Exception {
        Message message = ctx.bodyAsClass(Message.class);
        Message createdMessage = service.createMessage(message);
        ctx.json(createdMessage);
    }

    private void getAllMessages(Context ctx) throws Exception {
        List<Message> messages = service.getAllMessages();
        ctx.json(messages);
    }

    private void getMessageById(Context ctx) throws Exception {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Message message = service.getMessageById(id);
        ctx.json(message);
    }

    private void deleteMessageById(Context ctx) throws Exception {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Message deletedMessage = service.deleteMessageById(id);
        ctx.json(deletedMessage);
    }
}