package Client;

import Client.CLI.ClientCLI;
import Server.Server;

import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    /**
     * This main method display into the standard output console which interface (cli or gui or even server) we want to run and,
     * according to the choice, it starts the ClientCLI or the ClientGUI.
     * @param args is the Standard String vector that main must have
     */
    public static void main( String[] args ) throws IOException, ClassNotFoundException {
    ClientInterface view;
    Scanner stdin = new Scanner(System.in);
    System.out.println("Which graphical interface would you like to run, CLI or GUI?\n" +
            "If you would like to run the server instead just type SERVER.\n" +
            "Type EXIT instead if you want to quit the game");
    String selection = stdin.next();
    selection.toUpperCase(Locale.ROOT);
    boolean validSelection = false;

    do
    {
        switch (selection) {
            case "CLI":
                validSelection = true;
                view = new ClientCLI();
                view.run();
                break;
            /*case "GUI":
                validSelection = true;
                view = new ClientGUI();
                view.run();
                break;*/
            case "SERVER":
                validSelection = true;
                Server server;
                try {
                    server = new Server();
                    server.run();
                } catch (IOException e) {
                    System.err.println("Impossible to start the server!\n" + e.getMessage());
                    throw new RuntimeException(e);
                }
            case "EXIT":
                System.exit(0);
            default:
                System.out.println("Not a valid selection");
                System.out.println("Which graphical interface would you like to run, CLI or GUI? If you would like to run the server instead just type SERVER");
                selection = stdin.next();
                selection.toUpperCase(Locale.ROOT);
                break;
        }
    }while(!validSelection);
}
}

