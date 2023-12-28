package Client.Messages.ActionMessages;

import Client.Messages.Message;

/**
 * Abstract class from which the action messages inherit Type attribute and getter method
 */
public abstract class ActionMessage implements Message
{
    public ActionMessageENUM type;

    public ActionMessageENUM getType() {
        return type;
    }
}
