package Client.CLI;

import Server.CHandler;
import Server.Match;

import java.util.ArrayList;
import java.util.List;

public class Printer {

    public void printNationsInWorld()
    {
        //current will be the reference of the currentGame
        //current.getMapWorld().getNations().stream().map(t->t.getName()).forEach(System.out::println);
    }

    public static List<String> printListLobbySetup(ArrayList<Match> matches) {
        List<String> lobbyList = new ArrayList<>();
        for (Match match : matches) {
            String s="";

            for(CHandler cli: match.getClients())
            {
                s= s+cli.getNickname()+" ";
            }
            lobbyList.add("Lobby " + match.getMatchId() + ": " + match.getClients().size() + " players.\n Player: "+s+"\n");
        }
        return lobbyList;
    }
}
