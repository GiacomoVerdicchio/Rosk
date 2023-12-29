package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server  {

    private static final int PORT = 2509;
    private final ServerSocket server;
    private ArrayList<Match> matches ;

    public Server() throws IOException
    {
        this.server = new ServerSocket(PORT);
        this.matches = new ArrayList<>();
    }

    public void run() throws IOException
    {
        System.out.println("Server Started!");
        while (true) {
            try {

                Socket socket = server.accept();
                System.out.println("New Connection!");

                CHandler cHandler = new CHandler(socket);
                cHandler.setServerReference(this);
                cHandler.start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



    public Match createNewMatch(int num, CHandler cHandler) {
        Match m = new Match(matches.size(), num);
        m.addClient(cHandler);
        matches.add(m);
        cHandler.setHost(true);
        return m;
    }
    public Match addPersonToAMatch(int idMatch, CHandler cHandler) {

        Match m=matches.stream().filter(t->t.getMatchId() == idMatch).findFirst().orElse(null);
        if(m!=null) {
            m.addClient(cHandler);
            if (m.getClients().size() == 0) cHandler.setHost(true);
        }
        return m;
    }
    public void removeMatch(Match match){
        matches.remove(match);
    }
    public ArrayList<Match> getMatches() {
        return (ArrayList<Match>) matches.clone();
    }
}





























//have a map that say if a player is attacked ?????
//          have a map that say if a player is attacked ????????

//attackAsk (Nation to begin, Nation to attack, number of troops)-> void
//              verify using a static function of the controller:

//              that (begin is owned by him, attack is not owned by him
//                           call to isLinkedTerritory to see if are connected
//                           begin as at least 2 troops,
//              then
//              if(num troop>1)
//                  send request to enemy to retrieve num of troops to defend with
//               else
//                  computeAttack(Nation begin, nation end, attack with, defend with);

//           defendWith(Nation to defend, attacked with num troops)-> int
//
//when I have attack and defend -> (after checking the funcion attack defend)
//                                   computeAttack(Nation attack,defense, Troops attack,defend) in the controller
//               after the computing if it returns true-> movement troops function
//                                                 false-> continue with normal flow



//the server should update the phase counter (on each pahse)