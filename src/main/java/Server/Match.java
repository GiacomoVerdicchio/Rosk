package Server;

import Controller.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Match {
    private int matchId;
    private Controller controller;
    private int numPlayersDesired;
    private ArrayList<CHandler> clients = new ArrayList<>();
    private Server serverReference;
    private ExecutorService clientPinger = Executors.newFixedThreadPool(128);
    private HashMap<CHandler,Boolean> ready;
    private boolean gameStarted;

    public Match(int matchId, int numPlayersDesired, Server s) {
        this.matchId = matchId;
        this.controller=new Controller();
        this.numPlayersDesired =numPlayersDesired;
        this.ready = new HashMap<CHandler,Boolean>();
        this.gameStarted=false;
        this.serverReference=s;
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
                ready.putIfAbsent(cHand,false);
            }
        }
        ready.put(cHandler, true);
    }


    public void end()
    {
        serverReference.removeMatch(this);
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
