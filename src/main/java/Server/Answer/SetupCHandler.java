package Server.Answer;

import Client.CLI.Printer;
import Client.Messages.SetupMessages.*;
import Controller.MineException;
import Server.Answer.Setup.*;
import Server.CHandler;
import Server.ClientConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class SetupCHandler {

    private CHandler cHandler;
    public SetupCHandler(CHandler cHandler) {
        this.cHandler = cHandler;
    }


    public void handle(SetupMessage message) throws IOException, MineException {
        switch (message.getType()) {
            case SETUP_NAME:
                handleSetupName((SetupName) message);
                break;

            case CREATE_OR_JOIN:
                handleCreateOrJoin((CreateOrJoinMessage) message);
                break;

            case CHOSEN_LOBBY:
                handleChosenLobby((ChosenLobby) message);
                break;

            case READY_HOST:
                handleReadyHost();
                break;

            case READY_GUEST:
                handleReadyGuest();
                break;

            default:
                handleUnknownMessageType(message.getType());
        }

    }

    private void handleSetupName(SetupName setupName) {
        cHandler.setNickname(setupName.getNickname());
        // Handle SETUP_NAME logic
    }

    private void handleCreateOrJoin(CreateOrJoinMessage createOrJoinMessage) throws IOException {
        boolean create = createOrJoinMessage.isCreateTrue();
        if (create) {
            handleCreateGame(createOrJoinMessage);
        } else {
            handleJoinGame();
        }
    }

    private void handleCreateGame(CreateOrJoinMessage createOrJoinMessage) throws IOException {
        int numPlayersDesired = createOrJoinMessage.getNum();
        cHandler.setCurrentMatch(cHandler.getServerReference().createNewMatch(numPlayersDesired, cHandler));
        System.out.println("Created a game with players: " + cHandler.getNickname() + " in match : " + cHandler.getCurrentMatch());
        System.out.println();
    }

    private void handleJoinGame() {
        ListOfLobbies listOfLobbies;
        if (cHandler.getServerReference().getMatches().size() != 0)
            listOfLobbies = new ListOfLobbies(
                    (ArrayList<String>) Printer.printListLobbySetup(cHandler.getServerReference().getMatches()),
                    cHandler.getServerReference().getMatches().stream().map(t -> t.getMatchId())
                            .sorted(Comparator.reverseOrder()).findFirst().get());
        else {
            listOfLobbies = new ListOfLobbies(new ArrayList<>(), 0);
        }
        cHandler.getClientConnection().sendAnswer(new SerializedAnswer(listOfLobbies));
    }

    private void handleChosenLobby(ChosenLobby chosenLobby) throws IOException {
        int idLobbyChosen = chosenLobby.getId();
        cHandler.setCurrentMatch(cHandler.getServerReference().addPersonToAMatch(idLobbyChosen, cHandler));

        for (CHandler c : cHandler.getCurrentMatch().getClients()) {
            UpdateListPlayers listOfPlayersInThisMatch = new UpdateListPlayers
                    ((ArrayList<String>) cHandler.getCurrentMatch().getClients().stream().map(t -> t.getNickname())
                            .collect(Collectors.toList()));
            c.getClientConnection().sendAnswer(new SerializedAnswer(listOfPlayersInThisMatch));
        }
        System.out.println("Added a player: " + cHandler.getNickname() + " in match : " + cHandler.getCurrentMatch());
        System.out.println();
        if (cHandler.getCurrentMatch().getClients().size() == cHandler.getCurrentMatch().getNumPlayersDesired()) {
            cHandler.getCurrentMatch().getClients().stream().filter(t -> t.isHost()).findFirst()
                    .get().getClientConnection().sendAnswer(new SerializedAnswer(new StartReadyPhase()));
        }
    }

    private void handleReadyHost() {
        cHandler.getCurrentMatch().putReady(cHandler);
        cHandler.getCurrentMatch().setGameStarted(true);
        cHandler.getCurrentMatch().getClients().stream().filter(x -> !x.getClientConnection().equals(cHandler.getClientConnection()))
                .forEach(x -> x.getClientConnection().sendAnswer(new SerializedAnswer(new ReadyRequestFromHost())));
    }

    private void handleReadyGuest() throws MineException {
        System.out.println("Guest ready: " + cHandler.getNickname());
        boolean allReady = true;
        cHandler.getCurrentMatch().putReady(cHandler);

        ArrayList<String> listReadyToString = new ArrayList<>();
        for (CHandler c : cHandler.getCurrentMatch().getReady().keySet()) {
            listReadyToString.add(c.getNickname() + ": " + cHandler.getCurrentMatch().getReady().get(c));
        }
        cHandler.getCurrentMatch().getClients()
                .forEach(x -> x.getClientConnection().sendAnswer(new SerializedAnswer(new UpdateReadyPlayers(listReadyToString))));

        for (CHandler c : cHandler.getCurrentMatch().getReady().keySet()) {
            if (!cHandler.getCurrentMatch().getReady().get(c))
                allReady = false;
        }
        if (allReady)
        {
            //send start game to all
            cHandler.getCurrentMatch().getClients()
                    .forEach(x -> x.getClientConnection().sendAnswer(new SerializedAnswer(new StartGame())));
            //initializing the controller

            initializeGame();
        }
    }

    private void initializeGame() throws MineException {
        cHandler.setSetupFinished(true);
        //adding all the players to the controller
        for(CHandler c : cHandler.getCurrentMatch().getClients())
        {
            cHandler.getCurrentMatch().getController().getSetupController().addPlayer(c.getNickname());
        }
        //all players ready
        for(CHandler c : cHandler.getCurrentMatch().getClients())
        {
            cHandler.getCurrentMatch().getController().getSetupController().addReady(c.getNickname());
        }


        //TODO non so se è giusto qui ma in caso lo sposto prima
        for(CHandler client : cHandler.getCurrentMatch().getClients())
        {
            cHandler.getCurrentMatch().getController().getCurrentGame().addObserver(client);
        }

        //general setup
        cHandler.getCurrentMatch().getController().generalSetupAfterReady();

    }

    private void handleUnknownMessageType(SetupMessageENUM messageType) {
        // Handle unknown message type logic
        System.err.println("Unknown message type: " + messageType);
    }

}