package Client.CLI;

import Client.ClientInterface;
import Client.Messages.SerializedMessage;
import Client.Messages.SetupMessages.ChosenLobby;
import Client.Messages.SetupMessages.CreateOrJoinMessage;
import Client.Messages.SetupMessages.ReadyHost;
import Client.Messages.SetupMessages.SetupName;
import Server.Answer.Action.ActionAnswer;
import Server.Answer.SerializedAnswer;
import Server.Answer.Setup.ListOfLobbies;
import Server.Answer.Setup.SetupAnswer;
import Server.Answer.Setup.SetupAnswerENUM;
import Server.Answer.Setup.UpdateListPlayers;
import Server.Match;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientCLI implements ClientInterface {
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

        boolean loop=false;
        do {
            loop = false;
            String choice="";
            System.out.println("Do you want to create a match? (Otherwise you will join one)  [Yes,No]");
            choice=scanner.nextLine();
            if (choice.equals("Yes") || choice.equals("YES") || choice.equals("yes") || choice.equals("y"))
            {
                int num;
                do {
                    System.out.println("Specify how many players do you want?  [Between 3 and 6]");
                    num= scanner.nextInt();
                }while (num<3 || num>6);
                serverConnection.sendMessage(new SerializedMessage(new CreateOrJoinMessage(true, num)));
            }
            else if (choice.equals("No") || choice.equals("NO") || choice.equals("no") || choice.equals("n")) {
                serverConnection.sendMessage(new SerializedMessage(new CreateOrJoinMessage(false)));
                try {
                    SetupAnswer answer = ((SerializedAnswer) serverConnection.getIn().readObject()).getCommand();
                    if(answer.type== SetupAnswerENUM.LIST_LOBBIES)
                    {
                        ArrayList<String> listOfLobbies=((ListOfLobbies) answer).getList();
                        System.out.println(listOfLobbies);
                        System.out.println("Choose the id lobby where to join ? [-1 to exit]");
                        int num = scanner.nextInt();
                        if(num>=0 && ((ListOfLobbies) answer).getMaxMatchId() <=num ) {
                            serverConnection.sendMessage(new SerializedMessage(new ChosenLobby( num)));
                        }
                        else loop=true;
                    }
                    else loop=true;
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            else {loop = true; scanner.nextLine(); }
        }while (loop);

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


    //TODO insert all the setup messages
    @Override
    public void setupHandler(SetupAnswer answer){
        scanner=new Scanner(System.in);
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
                if (choice.equals("Yes") || choice.equals("YES") || choice.equals("yes") || choice.equals("y")) {
                    serverConnection.sendMessage(new SerializedMessage(new ReadyHost()));
                }else {
                    System.out.println("I will ask you only when a player will leave or join");
                }
                break;

            case READY_NEED_GUEST:
                System.out.println("Guest, are you ready? [Yes,No]");
                String choice2=scanner.nextLine();
                if (choice2.equals("Yes") || choice2.equals("YES") || choice2.equals("yes") || choice2.equals("y")) {
//                    serverConnection.sendMessage(new SerializedMessage(new ReadyGuest()));
                }else {
                    System.out.println("I will ask you only when a player will leave or join");
                }
                break;
        }
    }

    @Override
    public void messageHandler(ActionAnswer answer) {

        switch(answer.getType())
        {
            //TODO insert all the normal messages
        }
    }



    public ServerConnection getMain() {
        return serverConnection;
    }
}
