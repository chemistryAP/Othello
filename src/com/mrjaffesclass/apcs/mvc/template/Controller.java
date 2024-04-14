package com.mrjaffesclass.apcs.mvc.template;
import com.mrjaffesclass.apcs.messenger.*;

public class Controller implements MessageHandler {

    private final Messenger mvcMessaging;

    public Controller() {
        mvcMessaging = new Messenger();
        View view = new View(mvcMessaging);
        view.init();
        view.setVisible(true);
        Model model = new Model(mvcMessaging);
        model.init();
    }

    public void init() {
    }

    @Override
    public void messageHandler(String messageName, Object messagePayload) {
        if (messagePayload != null) {
            System.out.println("MSG: received by controller: " + messageName + " | " + messagePayload.toString());
        } else {
            System.out.println("MSG: received by controller: " + messageName + " | No data sent");
        }
    }

    public static void main(String[] args) {
        Controller app = new Controller();
        app.init();
    }
}
