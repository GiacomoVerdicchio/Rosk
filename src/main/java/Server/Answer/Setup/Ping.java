package Server.Answer.Setup;

/**
 * Ping message, useful to the client to understand if the server is alive
 */
public class Ping extends SetupAnswer{

    public Ping()
    {
        super.type = SetupAnswerENUM.PING;
    }
}

