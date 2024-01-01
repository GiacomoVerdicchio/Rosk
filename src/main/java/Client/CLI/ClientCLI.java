package Client.CLI;

import Client.ClientInterface;
import Client.InformationGenerator;
import Client.Messages.SerializedMessage;
import Client.Messages.SetupMessages.*;
import Server.Answer.Action.ActionAnswer;
import Server.Answer.Action.ErrorMessage;
import Server.Answer.Action.ViewMessage;
import Server.Answer.SerializedAnswer;
import Server.Answer.Setup.*;
import Server.Match;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ClientCLI implements ClientInterface , InformationGenerator {
    private ServerConnection serverConnection;
    private Scanner scanner = new Scanner(System.in);
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public ClientCLI() {
    }

    @Override
    public void run() {

        System.out.println("ServerIP?");
        String IP = scanner.nextLine();
        serverConnection = new ServerConnection(IP);
        try {
            serverConnection.establishConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Nickname?");
        String nickname = scanner.nextLine();
        serverConnection.sendMessage(new SerializedMessage(new SetupName(nickname)));

        boolean loopExit=false;
        String choiceInInput;
        int numInInput;
        do {
            loopExit = false;
            scanner=new Scanner(System.in);
            System.out.println("Do you want to create a match? (Otherwise you will join one)  [Yes,No]");
            choiceInInput=scanner.nextLine();
            if (choiceInInput.toLowerCase().equals("yes") || choiceInInput.equals("y"))
            {
                do {
                    System.out.println("Specify how many players do you want?  [Between 3 and 6]");
                    while (!scanner.hasNextInt()) {
                        System.out.println("Invalid input. Please enter a valid number.");
                        scanner.next();
                    }
                    numInInput= scanner.nextInt();
                }while (numInInput<3 || numInInput>6);
                serverConnection.sendMessage(new SerializedMessage(new CreateOrJoinMessage(true, numInInput)));
            }

            else if (choiceInInput.toLowerCase().equals("no") || choiceInInput.equals("n"))
            {
                serverConnection.sendMessage(new SerializedMessage(new CreateOrJoinMessage(false)));
                try {
                    SetupAnswer answer = ((SerializedAnswer) serverConnection.getIn().readObject()).getCommand();

                    if(answer.type== SetupAnswerENUM.LIST_LOBBIES)
                    {
                        ArrayList<String> listOfLobbies=((ListOfLobbies) answer).getList();
                        System.out.println(listOfLobbies);
                        System.out.println("Choose the id lobby where to join ? [-1 to exit]");
                        while (!scanner.hasNextInt()) {
                            System.out.println("Invalid input. Please enter a valid number.");
                            scanner.next();
                        }
                        numInInput = scanner.nextInt();
                        if(numInInput>=0 && ((ListOfLobbies) answer).getMaxMatchId() <=numInInput ) {
                            serverConnection.sendMessage(new SerializedMessage(new ChosenLobby( numInInput)));
                        }
                        else loopExit=true;
                    }

                    else loopExit=true;
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            else {loopExit = true;}
        }while (loopExit);

        ListenerCLI Listener = new ListenerCLI(this);
        //this.stdin = new InputParser(main, MyView);
        executor.execute(Listener);
        //Ho cacncellato molta roba con il synchronized
    }

    @Override
    public void readMessage() {
        try
        {
            SerializedAnswer input = (SerializedAnswer) serverConnection.getIn().readObject();
            if(input.getCommand() != null)
            {
                SetupAnswer answer = input.getCommand();
                setupHandler(answer);
            }
            if(input.getAction() != null)
            {
                ActionAnswer answer = input.getAction();
                messageHandler(answer);
            }
        }
        catch(SocketTimeoutException e)
        {
            serverConnection.disconnect();
            System.out.println("Connection to server lost");
            System.exit(0);
        }
        catch(IOException e)
        {
            serverConnection.disconnect();
            System.out.println("Something went wrong, your game was stopped");
            System.exit(0);
        }
        catch(ClassNotFoundException e)
        {
            System.out.println("Client couldn't understand Server");
        }
    }


    @Override
    public void setupHandler(SetupAnswer answer) throws IOException {
        scanner=new Scanner(System.in);
        //TODO inserire anche qui (come già c'è nel CHAnlder)) l'if di controllo sul setupPhase
        switch(answer.getType())
        {
            case UPDATE_LIST_PLAYERS :
                ArrayList<String> list=( (UpdateListPlayers) answer).getList();
                for(String player : list)
                {
                    System.out.print(player+" ");
                }
                System.out.println(";");
                break;

            case START_READY_PHASE:
                System.out.println("Host, are you ready? [Yes,No]");
                String choice=scanner.nextLine();
                if (choice.toLowerCase().equals("yes") || choice.equals("y")) {
                    serverConnection.sendMessage(new SerializedMessage(new ReadyHost()));
                }else {
                    System.out.println("I will ask you only when a player will leave or join");
                    //TODO mettere messaggio uscita
                }
                break;

            case READY_NEED_GUEST:
                System.out.println("Guest, are you ready? [Yes,No]");
                String choice2=scanner.nextLine();
                if (choice2.toLowerCase().equals("yes") || choice2.equals("y")) {
                    serverConnection.sendMessage(new SerializedMessage(new ReadyReplyFromGuest()));
                }else {
                    System.out.println("I will ask you only when a player will leave or join");
                    //todo mettere messaggio uscita e killare il proc
                }
                break;

            case UPDATE_READY_PLAYERS:
                System.out.println(( (UpdateReadyPlayers) answer).getList());
                break;

            case START_GAME:
                Printer.cls();
                System.out.println("Il gioco sta iniziando!");
                //TODO inserire setyup a false
                break;

            case DISCONNECT:
                System.out.println(( (Disconnect) answer).getMessage());
                System.exit(0);
        }
    }

    @Override
    public void messageHandler(ActionAnswer answer) {

        switch(answer.getType())
        {
            case ERROR_MESSAGE:
                System.out.println(errorGenerator(((ErrorMessage)answer).getError(), MyView));
                break;
            case VIEW:
                try
                {
                    MyView.parse((ViewMessage) answer);
                    /*stdin.printGame();
                    System.out.println(MyView.getCurrentTurnState().getGamePhase());
                    if(main.getNickname().equals(MyView.getCurrentTurnState().getCurrentPlayer()))
                    {
                        System.out.println(informationCreator(MyView.getCurrentTurnState(), MyView.getCurrentTeams()).getInfoMessage());
                    }*/
                }
                catch(JsonProcessingException e)
                {
                    System.out.println("Error in processing view, show commands aren't available");
                }
                break;

        }
    }



    public ServerConnection getMain() {
        return serverConnection;
    }
}
