package Server.Answer;

import Server.Answer.Action.ActionAnswer;
import Server.Answer.Setup.SetupAnswer;

import java.io.Serializable;

/**
 * Class containing the methods to form a serialized server answer
 */
public class SerializedAnswer implements Serializable
{
    private static final long serialVersionUID = 7526472295622776147L;
    private SetupAnswer command;
    private ActionAnswer action;

    /**
     * Constructor for a game message
     */
    public SerializedAnswer(ActionAnswer m)
    {
        this.command = null;
        this.action = m;
    }

    /**
     * Constructor for a setup message
     */
    public SerializedAnswer(SetupAnswer m)
    {
        this.command = m;
        this.action = null;
    }

    public SetupAnswer getCommand() {
        return command;
    }

    public ActionAnswer getAction() {
        return action;
    }
}
