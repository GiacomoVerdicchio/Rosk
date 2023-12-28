package Server.Answer.Setup;

import Client.Messages.SetupMessages.SetupMessage;
import Client.Messages.SetupMessages.SetupMessageENUM;
import Server.Answer.Setup.SetupAnswer;
import Server.Answer.Setup.SetupAnswerENUM;

public class Disconnect extends SetupAnswer {
    private String message;

    public Disconnect(String message)
    {
        this.message = message;
        super.type = SetupAnswerENUM.DISCONNECT;
    }

    public String getMessage() {
        return message;
    }
}
