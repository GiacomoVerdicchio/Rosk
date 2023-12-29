package Server;

import Client.CLI.Printer;
import Client.Messages.ActionMessages.ActionMessage;
import Client.Messages.SerializedMessage;
import Client.Messages.SetupMessages.*;
import Controller.Controller;
import Observer.Observer;
import Server.Answer.SerializedAnswer;
import Server.Answer.Setup.*;

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
    private Controller controller;
    private Match currentMatch;
    private boolean isHost;

    private boolean isAlive=true;

    public CHandler(Socket clientConnection) throws IOException {
        ClientConnection connection = new ClientConnection(clientConnection);
        this.clientConnection = connection;
        nickname="";
        isHost = false;
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
    public void run()
    {
        while(isAlive)
        //while(isAlive())
        {
            if(connected)
                readMessage();
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
    public void readMessage()
    {
        try
        {
            SerializedMessage input = (SerializedMessage) clientConnection.getInputStream().readObject();
            if(input.getCommand() != null)
            {
                SetupMessage message = input.getCommand();
                setupHandler(message);
            }
            if(input.getAction() != null)
            {
                ActionMessage message = input.getAction();
                messageHandler(message);
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




    /**
     * method setupHandler handles the messages that could arrive during the setupPhase, team choice, wizard choice and
     * player readiness
     * @param message the message that needs to be handled
     * @throws IOException if closing the connection results in an error
     */
    public void setupHandler(SetupMessage message) throws IOException
    {
        //controller.gamePhase is setup
        if( true)
            switch(message.type)
            {
                case SETUP_NAME :
                    nickname = ((SetupName)message).getNickname();
                    break;

                case CREATE_OR_JOIN:
                    boolean create = ((CreateOrJoinMessage)message).isCreateTrue();
                    if(create)
                    {
                        int numPlayersDesired=((CreateOrJoinMessage)message).getNum();
                        currentMatch=serverReference.createNewMatch(numPlayersDesired, this);
                        System.out.println("Created a game with players: "+ nickname+" in match : "+ currentMatch);
                        System.out.println();
                    }
                    else {
                        ListOfLobbies listOfLobbies;
                        if(serverReference.getMatches().size()!=0)
                            listOfLobbies = new ListOfLobbies(
                                (ArrayList<String>) Printer.printListLobbySetup(serverReference.getMatches()),
                                serverReference.getMatches().stream().map(t->t.getMatchId())
                                        .sorted(Comparator.reverseOrder()).findFirst().get());
                        else  {
                            listOfLobbies =new ListOfLobbies(new ArrayList<>(),0);
                        }
                        clientConnection.sendAnswer(new SerializedAnswer(listOfLobbies));
                    }
                    break;


                case CHOSEN_LOBBY:
                    int idLobbyChosen = ((ChosenLobby)message).getId();
                    currentMatch = serverReference.addPersonToAMatch(idLobbyChosen, this);

                    for(CHandler c : currentMatch.getClients())
                    {
                        UpdateListPlayers listOfPlayersInThisMatch=new UpdateListPlayers
                                ((ArrayList<String>) currentMatch.getClients().stream().map(t->t.nickname)
                                        .collect(Collectors.toList()));
                        c.clientConnection.sendAnswer(new SerializedAnswer(listOfPlayersInThisMatch));
                    }
                    System.out.println("Added a player: "+ nickname+" in match : "+ currentMatch);
                    System.out.println();
                    if(currentMatch.getClients().size() == currentMatch.getNumPlayersDesired())
                    {
                        currentMatch.getClients().stream().filter(t->t.isHost).findFirst()
                                .get().getClientConnection().sendAnswer(new SerializedAnswer(new StartReadyPhase()));
                    }
                    break;

                case READY_HOST:
                        currentMatch.putReady(this);
                        currentMatch.setGameStarted(true);
                        currentMatch.getClients().stream().filter(x->! x.clientConnection.equals(this.clientConnection))
                                .forEach(x->x.getClientConnection().sendAnswer(new SerializedAnswer(new ReadyRequestFromHost())));
                    break;

                case READY_GUEST:
                    System.out.println("Guest ready: "+getNickname());
                    boolean allReady = true;
                    currentMatch.putReady(this);

                    ArrayList<String> listReadyToString=new ArrayList<String>();
                    for(CHandler c: currentMatch.getReady().keySet())
                    {
                        listReadyToString.add(c.nickname+": "+currentMatch.getReady().get(c));
                    }
                    currentMatch.getClients().stream()
                            .forEach(x->x.getClientConnection().sendAnswer(new SerializedAnswer(new UpdateReadyPlayers(listReadyToString))));

                    for(CHandler c: currentMatch.getReady().keySet())
                    {
                        if(! currentMatch.getReady().get(c))
                            allReady=false;
                    }
                    if(!allReady)
                        System.out.println("allready false");
                    if(allReady) {
                        System.out.println("allready true");
                        currentMatch.getClients().stream()
                                .forEach(x -> x.getClientConnection().sendAnswer(new SerializedAnswer(new StartGame())));
                    }
                    break;
            }
        else
        {
            //socket.sendAnswer(new SerializedAnswer(new ErrorMessage(ERRORTYPES.WRONG_PHASE)));
        }

    }

    /**
     * Method messageHandler decides wheter to redirect the handling of the game messages to the planningHandler method
     * or the actionHandler
     * @param message the message that needs to be handles
     */
    public void messageHandler(ActionMessage message)
    {
        if( true//something
             )
        {

        }
        else
        {
            //socket.sendAnswer(new SerializedAnswer(new ErrorMessage(ERRORTYPES.WRONG_TURN)));
        }
    }



    @Override
    public void update(String message)
    {
        System.out.println(message);
        // socket.sendAnswer(new SerializedAnswer(new ViewMessage(message, mainController.getGame().getCurrentCharacterDeck(), mainController.getGame().getCurrentActiveCharacterCard(), mainController.isExpertGame())));
    }
    public void setHost(boolean host) {
        isHost = host;
    }
    public String getNickname() {
        return nickname;
    }
    public boolean isHost() {
        return isHost;
    }
    public ClientConnection getClientConnection()
    {
        return clientConnection;
    }
    public Server getServerReference() {
        return serverReference;
    }
    public void setServerReference(Server serverReference) {
        this.serverReference = serverReference;
    }
}
