package Client.Messages.SetupMessages;

import Client.Messages.Message;

/**
 * Abstract class from which all setup messages inherit Type and relative getter
 */
public abstract class SetupMessage implements Message
{
    public SetupMessageENUM type;

    public SetupMessageENUM getType(){return type;}
}
