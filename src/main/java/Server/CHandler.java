package Server;

import Client.CLI.Printer;
import Client.Messages.ActionMessages.ActionMessage;
import Client.Messages.SerializedMessage;
import Client.Messages.SetupMessages.*;
import Controller.MineException;
import Observer.Observer;
import Server.Answer.Action.ErrorMessage;
import Server.Answer.Action.ErrorTypesENUM;
import Server.Answer.Action.ViewMessage;
import Server.Answer.ActionCHandler;
import Server.Answer.SerializedAnswer;
import Server.Answer.Setup.*;
import Server.Answer.SetupCHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;


public class CHandler extends Thread implements Observer
{
    private Server serverReference;
    private ClientConnection clientConnection;
    private boolean connected = true;
    private String nickname;
    private Match currentMatch;
    private boolean isHost;
    private boolean isAlive=true;

    private boolean isSetupFinished;
    private SetupCHandler setupMessageHandler;

    private ActionCHandler actionMessageHandler;



    public CHandler(Socket clientConnection) throws IOException {
        ClientConnection connection = new ClientConnection(clientConnection);
        this.clientConnection = connection;
        nickname="";
        isHost = false;
        isSetupFinished = false;
        this.setupMessageHandler = new SetupCHandler(this);
        this.actionMessageHandler=new ActionCHandler(this);
    }



    @Override
    public void run()
    {
        while(isAlive)
        {
            if(connected) {
                try {
                    readMessage();
                } catch (MineException e) {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                isAlive=false;
                handlingClosingConnection();
            }
        }
    }

    /**
     * Method readMessage reads messages coming from the client and redirects them to the correct handler
     */
    public void readMessage() throws MineException {
        try
        {
            SerializedMessage input = (SerializedMessage) clientConnection.getInputStream().readObject();
            if(input.getCommand() != null)
            {
                SetupMessage setupMessageFromClient = input.getCommand();
                //handles the messages that could arrive during the setupPhase
                if( ! isSetupFinished)
                {
                    setupMessageHandler.handle(setupMessageFromClient);
                }
                else
                {
                    clientConnection.sendAnswer(new SerializedAnswer(new ErrorMessage(ErrorTypesENUM.wrong_phase)));
                }
            }

            if(input.getAction() != null)
            {
                ActionMessage actionMessageFromClient = input.getAction();
                if( isSetupFinished)
                {
                    actionMessageHandler.handle(actionMessageFromClient);
                }
                else
                {
                    clientConnection.sendAnswer(new SerializedAnswer(new ErrorMessage(ErrorTypesENUM.wrong_phase)));
                }
            }
        }
        catch (IOException e)
        {
            System.out.println(nickname + " disconnected");
            connected = false;
        }
        catch(ClassNotFoundException e)
        {
            System.out.println("Couldn't understand what " + nickname + " was saying...");
        }
    }




    private void handlingClosingConnection(){
        if(currentMatch!=null)
        {
            if(currentMatch.isGameStarted() || this.isHost)
            {
                Disconnect disconnect = new Disconnect("You where disconnected because a client (or the host) exit the game");
                for(CHandler c: currentMatch.getClients())
                    c.clientConnection.sendAnswer(new SerializedAnswer(disconnect));
                currentMatch.endingConnection();
            }
            else {
                if(currentMatch.getClients().contains(this))
                    currentMatch.getClients().remove(this);
            }
        }
        this.interrupt();
    }

    @Override
    public void update(String message)
    {
        System.out.println("Content of view" + message);
        clientConnection.sendAnswer(new SerializedAnswer(new ViewMessage(message)));
        System.out.println();
        System.out.println();
    }
    public void setHost(boolean host) {isHost = host;}
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public void setServerReference(Server serverReference) {this.serverReference = serverReference;}
    public void setCurrentMatch(Match currentMatch) {
        this.currentMatch = currentMatch;
    }
    public void setSetupFinished(boolean setupFinished) {
        isSetupFinished = setupFinished;
    }

    public String getNickname() {return nickname;}
    public ClientConnection getClientConnection() {return clientConnection;}
    public Server getServerReference() {return serverReference;}
    public Match getCurrentMatch() {
        return currentMatch;
    }
    public boolean isHost() {
        return isHost;
    }
}
