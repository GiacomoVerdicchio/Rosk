package Client.Messages;

import Client.Messages.ActionMessages.ActionMessage;
import Client.Messages.SetupMessages.SetupMessage;

import java.io.Serializable;

/**
 * Class containing the methods to form a serialized message
 */
public class SerializedMessage implements Serializable
{
    private static final long serialVersionUID = 7526472295622776147L;
    private SetupMessage command;
    private ActionMessage action;

    /**
     * Constructor for a game message
     */
    public SerializedMessage(ActionMessage m)
    {
        this.command = null;
        this.action = m;
    }

    /**
     * Constructor for a setup message
     */
    public SerializedMessage(SetupMessage m)
    {
        this.command = m;
        this.action = null;
    }

    public SetupMessage getCommand() {
        return command;
    }

    public ActionMessage getAction() {
        return action;
    }
}
