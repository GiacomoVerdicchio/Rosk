package Server.Answer;

import Client.Messages.ActionMessages.ActionMessage;
import Server.CHandler;

import java.io.IOException;

public class ActionCHandler {


    private CHandler cHandler;
    public ActionCHandler(CHandler cHandler) {
        this.cHandler = cHandler;
    }



    public void handle(ActionMessage message) throws IOException {
        switch (message.getType()) {
            /*
            case SETUP_NAME:
                handleSetupName((SetupName) message);
                break;
            */
        }

    }

/*

    private void handleSetupName(SetupName setupName) {
        cHandler.setNickname(setupName.getNickname());
        // Handle SETUP_NAME logic
    }
    */
}
