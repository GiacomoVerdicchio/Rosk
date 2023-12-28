package Server;

import Server.Answer.SerializedAnswer;
import Server.Answer.Setup.Ping;

/**
 * Class ClientPinger handles the pinging of the various clients in the game
 */
public class ClientPinger implements /*always*/ Runnable
{
    private CHandler client;

    public ClientPinger(CHandler client)
    {
        this.client = client;
    }

    @Override
    public void run()
    {
        while(true)
        {
            client.getClientConnection().sendAnswer(new SerializedAnswer(new Ping()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
