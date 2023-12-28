package Client.CLI;

/**
 * Class ListenerCLI handles the hard task of listening to various server messages
 */
public class ListenerCLI implements Runnable
{
    private ClientCLI client;

    /**
     * Class Constructor instantiates a new Listener for the CLI
     */
    public ListenerCLI(ClientCLI client)
    {
        this.client = client;
    }

    /**
     * Method run reads messages incoming from the server while the client is connected
     */
    @Override
    public void run()
    {
        while(client.getMain().getConnected())
        {
            client.readMessage();
        }
    }
}
