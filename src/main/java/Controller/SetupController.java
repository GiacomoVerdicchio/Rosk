package Controller;

import Model.*;

import java.util.*;

public class SetupController {

    private CurrentGame currentGame;
    private HashMap <String,Boolean> ready;

    public SetupController(CurrentGame currentGame) {
        this.currentGame = currentGame;
        ready = new HashMap<>();
    }




    public void addPlayer(String name)
    {
        currentGame.addPlayer(name);
        ready.putIfAbsent(currentGame.getPlayers().stream()
                .filter(t-> (t.getName().equals(name))).findFirst().get().getName() ,false);
    }
    public void removePlayer(String name)
    {
        currentGame.removePlayer(name);
        ready.keySet().remove(name);
    }



    private void checksForPlayerSize(String name) throws MineException {
        if(currentGame.getPlayers().size() == 0) {throw new MineException("No player in the game");}
        if(!ready.keySet().contains(name))
            throw new MineException("The player " + name + " is not in the list of players");
    }
    public void addReady(String name) throws MineException{
        checksForPlayerSize(name);
        ready.put(name,true);
    }
    public void removeReady(String name) throws MineException{
        checksForPlayerSize(name);
        ready.put(name,false);
    }

    public boolean isReadyAllPlayers(){
        if(! Checks.checkMaxNumberOfPlayers(ready.size())) return false;


        for(String name: ready.keySet())
        {
            if(ready.get(name).equals(false))
                return false;
        }
        return true;
    }


    public void setAllIdOfPlayers()
    {
        int i=0;
        for(Player p : currentGame.getPlayers())
        {
            p.setIdPlayer(i);
            i++;
        }
    }

    public HashMap<String, Boolean> getReady() {
        return ready;
    }
}
