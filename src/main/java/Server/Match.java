package Server;

import Controller.Controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Match {
    private int matchId;
    private Controller controller;
    private int numPlayersDesired;
    private ArrayList<CHandler> clients = new ArrayList<>();
    private ExecutorService clientPinger = Executors.newFixedThreadPool(128);
    private HashMap<CHandler,Boolean> ready;

    public Match(int matchId, int numPlayersDesired) {
        this.matchId = matchId;
        this.controller=new Controller();
        this.numPlayersDesired =numPlayersDesired;
        this.ready = new HashMap<CHandler,Boolean>();
    }




    public void addClient(CHandler cHandler) throws IOException {
        clients.add(cHandler);
        clientPinger.execute(new ClientPinger(cHandler));
    }

    public void putReady(CHandler cHandler)
    {
        if(ready.size()==0)
        {
            for(CHandler cHand:clients)
            {
                ready.put(cHand,false);
            }
        }
        ready.putIfAbsent(cHandler, true);
    }

    public ArrayList<CHandler> getClients() {
        return clients;
    }

    public int getMatchId() {
        return matchId;
    }

    public Controller getController() {
        return controller;
    }
    public int getNumPlayersDesired() {
        return numPlayersDesired;
    }
}
