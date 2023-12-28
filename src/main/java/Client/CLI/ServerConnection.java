package Client.CLI;

import Client.Messages.SerializedMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection
{
    private Socket server;
    private int PORT = 2509;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String ServerIP;
    private boolean connected = false;


    /**
     * Class Constructor, saves the parameters to send to the server after connecting
     */
    public ServerConnection( String serverIP)
    {
        this.ServerIP = serverIP;
    }

    /**
     * Method establishConnection tries to connect to the server
     */
    public void establishConnection() throws IOException {
        server = new Socket(ServerIP, PORT);
        out = new ObjectOutputStream(server.getOutputStream());
        in = new ObjectInputStream(server.getInputStream());
        connected = true;
        server.setSoTimeout(10000);
    }


    /**
     * Sends a SerializedMessage to the server
     */
    public void sendMessage(SerializedMessage answer)
    {
        try {
            out.writeObject(answer);
            out.flush();
            out.reset();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void disconnect()
    {
        this.connected = false;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public String getServerIP() {
        return ServerIP;
    }

    public boolean getConnected(){return connected;}

}
