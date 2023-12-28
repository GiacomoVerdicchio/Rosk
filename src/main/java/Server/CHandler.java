package Server;

import Client.CLI.Printer;
import Client.Messages.ActionMessages.ActionMessage;
import Client.Messages.SerializedMessage;
import Client.Messages.SetupMessages.ChosenLobby;
import Client.Messages.SetupMessages.CreateOrJoinMessage;
import Client.Messages.SetupMessages.SetupMessage;
import Client.Messages.SetupMessages.SetupName;
import Controller.Controller;
import Observer.Observer;
import Server.Answer.SerializedAnswer;
import Server.Answer.Setup.ListOfLobbies;
import Server.Answer.Setup.ReadyNeedGuest;
import Server.Answer.Setup.StartReadyPhase;
import Server.Answer.Setup.UpdateListPlayers;

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



    public CHandler(Socket clientConnection) throws IOException {
        ClientConnection connection = new ClientConnection(clientConnection);
        this.clientConnection = connection;
        nickname="";
        isHost = false;

        //voglio implementarlo nel match    this.ready = false;
    }




    @Override
    public void run()
    {

        while(isAlive())
        {
            if(connected)
            {
                readMessage();
            }
            else
            {
                //TODO
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
            if(currentMatch.getClients().contains(this))
                currentMatch.getClients().remove(this);
            if(isHost)
                serverReference.killMatch(currentMatch);
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
                    System.out.println("Entrati nel setupName del server");
                    nickname = ((SetupName)message).getNickname();
                    break;

                case CREATE_OR_JOIN:
                    System.out.println("Entrati nel CreateOrJoin del server");
                    boolean create = ((CreateOrJoinMessage)message).isCreateTrue();
                    if(create)
                    {
                        int numPlayersDesired=((CreateOrJoinMessage)message).getNum();
                        currentMatch=serverReference.createNewMatch(numPlayersDesired, this);
                        isHost=true;
                        System.out.println("Created a game with players, nick:"+ nickname);
                    }
                    else {
                        //allora mando la lista delle lobby (chiedendola al server) e aspetto che mi risponda,
                        // (quando otterrò la risposta allora chiamo il serverReference e gli aggiungo questa istanza al match
                        //  e mando un messaggio all'host per dire che ha joinato un altro partecipante (con attributo nome)???)
                        ListOfLobbies listOfLobbies=new ListOfLobbies(
                                (ArrayList<String>) Printer.printListLobbySetup(serverReference.getMatches()),
                                serverReference.getMatches().stream().map(t->t.getMatchId()).sorted(Comparator.reverseOrder()).findFirst().get());
                        clientConnection.sendAnswer(new SerializedAnswer(listOfLobbies));
                    }
                    break;

                //case della risposta tra le lobby
                case CHOSEN_LOBBY:
                    int idLobbyChosen = ((ChosenLobby)message).getId();
                    currentMatch = serverReference.addPersonToAMatch(idLobbyChosen, this);

                    for(CHandler c : currentMatch.getClients())
                    {
                        UpdateListPlayers listOfPlayersInThisMatch=new UpdateListPlayers
                                ((ArrayList<String>) currentMatch.getClients().stream().map(t->t.nickname).collect(Collectors.toList()));
                        c.clientConnection.sendAnswer(new SerializedAnswer(listOfPlayersInThisMatch));
                    }
                    if(currentMatch.getClients().size() == currentMatch.getNumPlayersDesired())
                    {
                        currentMatch.getClients().stream().filter(t->t.isHost).findFirst()
                                .get().getClientConnection().sendAnswer(new SerializedAnswer(new StartReadyPhase()));
                    }
                    System.out.println("Finished CHOSEN LOBBY");
                    break;

                case READY_HOST:
                        currentMatch.putReady(this);
                        currentMatch.getClients().stream()
                                .forEach(x->x.getClientConnection().sendAnswer(new SerializedAnswer(new ReadyNeedGuest())));
                    break;
                /*
                case READY_GUEST:
                    break;
                 */

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
    public void setServerReference(Server serverReference) {
        this.serverReference = serverReference;
    }
}
