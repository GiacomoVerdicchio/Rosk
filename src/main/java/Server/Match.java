package Server;

import Controller.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Match {
    private final int matchId;
    private Controller controller;
    private final int numPlayersDesired;
    private ArrayList<CHandler> clients = new ArrayList<>();
    private final ExecutorService clientPinger = Executors.newFixedThreadPool(128);
    private HashMap<CHandler,Boolean> ready;
    private boolean gameStarted;

    public Match(int matchId, int numPlayersDesired) {
        this.matchId = matchId;
        this.controller=new Controller();
        this.numPlayersDesired =numPlayersDesired;
        this.ready = new HashMap<>();
        this.gameStarted=false;
    }




    public void addClient(CHandler cHandler) {
        clients.add(cHandler);
        clientPinger.execute(new ClientPinger(cHandler));
    }

    public void putReady(CHandler cHandler)
    {
        if(ready.size()==0)
        {
            for(CHandler cHand:clients)
            {
                ready.putIfAbsent(cHand,false);
            }
        }
        ready.put(cHandler, true);
    }


    public void endingConnection()
    {
        Server s=clients.get(0).getServerReference();
        s.removeMatch(this);
        for(CHandler cH: this.getClients()){
            cH.interrupt();
        }
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
    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
    public boolean isGameStarted() {
        return gameStarted;
    }
    public HashMap<CHandler, Boolean> getReady() {
        return ready;
    }
}
