package Controller;

import Model.*;

import java.util.*;

public class Controller {
    //will divide all the cards to all the players at the beginnign
    CurrentGame currentGame;

    boolean winner;
    private SetupController setupController;
    private ActionController actionController;
    private GamePhase gamePhase;
    private HashMap<Integer,PhaseTurn> turnPhases;
    private int currentPlayerActing;
    private int modeOfWinning;

    public Controller() {
        currentGame = new CurrentGame();
        setupController = new SetupController(currentGame);
        actionController=new ActionController(currentGame);
        winner = false;
        gamePhase=GamePhase.setup;
        this.turnPhases = new HashMap<>();
        this.currentPlayerActing=-1;
        modeOfWinning =0; //basic domination game
    }



    public void updateGamePhase()
    {
        if(gamePhase.ordinal()!=3)
            gamePhase=GamePhase.values()[gamePhase.ordinal()+1];
    }

    private void setupTurnPhasesForAll()
    {
        for(Player p : currentGame.getPlayers())
        {
            turnPhases.put(p.getIdPlayer(), PhaseTurn.wait);
        }
        currentPlayerActing=0;
        turnPhases.put(currentPlayerActing,PhaseTurn.fortify);
        actionController.setTroopsToBeStillDeployedWithoutTer(currentPlayerActing);
        //TODO (non ha senso ma lo metto per ricordarmi che qui devo mettere tutte le istruzioni che metto anche sotto dopo il moveAtTheEnd
    }

    public boolean updateTurnPhase(int idPlayer)
    {
        if(idPlayer!= currentPlayerActing ) return false;
        if(isWinner()) return false;

        switch (turnPhases.get(currentPlayerActing))
        {
            case fortify:
                if(actionController.getTroopsYetToBeDeployed() != 0) return false;
                if(! Checks.checkForNoMoreThan5CardInRemainingDeck(currentGame.getPlayers().get(currentPlayerActing).getTerritoryCardsOwned())) return false;
                else {
                    turnPhases.put(currentPlayerActing, PhaseTurn.attack);
                }
                break;

            case attack:
                turnPhases.put(currentPlayerActing, PhaseTurn.moveAtTheEnd);
                break;

            case moveAtTheEnd:
                turnPhases.put(currentPlayerActing, PhaseTurn.wait);
                do {
                    currentPlayerActing = (currentPlayerActing + 1) % currentGame.getPlayers().size();
                }while (! Checks.getPlayerById(currentPlayerActing, currentGame.getPlayers()) .isAlive());
                turnPhases.put(currentPlayerActing, PhaseTurn.fortify);
                actionController.setTroopsToBeStillDeployedWithoutTer(currentPlayerActing);
                break;

            default:
                return false;
        }
        return true;
    }

    public void generalSetupAfterReady() throws MineException
    {
        if(!getSetupController().isReadyAllPlayers()) throw new MineException("Game cannot start because players not ready");
        getSetupController().setAllIdOfPlayers();
        currentGame.getMapWorld().fillMapWithSoldiers(currentGame.getPlayers().size(), currentGame.getTerritoriesDeck());

        //add of jolly at the end
        currentGame.getTerritoriesDeck().add(new TerritoryCard("jolly1",3,null, null));
        currentGame.getTerritoriesDeck().add(new TerritoryCard("jolly2",3,null, null));

        //copy all the cards in the remaining pile
        currentGame.refillDeckTerritoriesRemaining();

        updateGamePhase();
        setupTurnPhasesForAll();
    }



    public boolean addTroopsUsingTerritoryCards(int index1, int index2, int index3, int idPlayer ) throws RuntimeException
    {
        if(idPlayer!= currentPlayerActing) return false;
        if(turnPhases.get(currentPlayerActing)!= PhaseTurn.fortify) return false;
        if(! Checks.checkAreCardsPassedValid(index1, index2, index3, idPlayer, currentGame.getPlayers())) return false;

        actionController.useTerritoryCards(index1, index2, index3, idPlayer);
        return true;
    }


    public boolean addTroopsToFortify(NationsName nationToDeployTroops, int numOfTroops, int idOwner)
    {
        if(idOwner!= currentPlayerActing) return false;
        if(turnPhases.get(currentPlayerActing) != PhaseTurn.fortify) return false;

        Nation n=currentGame.getMapWorld().getNation(nationToDeployTroops);
        if( ! Checks.checkForAddTroopsToFortify(n,numOfTroops,idOwner, actionController.getTroopsYetToBeDeployed())) return false;;
        n.addTroops(numOfTroops);
        actionController.removeTroopsYetToBeDeployed(numOfTroops);

        return true;
    }

    public boolean attack(Nation start,Nation target, int attackTroops,int  defendTroops)
    {
        if(! Checks.checkForMaxTroopsNumber(start,target,attackTroops,defendTroops)) throw new RuntimeException();
        if(! Checks.checkForAttackAndDefend(start,target,attackTroops,defendTroops , gamePhase, turnPhases)) throw new RuntimeException();

        Player p =currentGame.getPlayers().get(target.getIdOwner());
        boolean res= actionController.computeAttack(start,target,attackTroops,defendTroops);

        if(res && ! currentGame.getMapWorld().getNations().stream().anyMatch(t-> t.getIdOwner()== p.getIdPlayer()) )
        {
            aPlayerDied(p);
        }
        if(res && modeOfWinning ==0)
        {
            int idPlayerMaybeWinner = currentGame.getMapWorld().getNations().get(0).getIdOwner();
            if(currentGame.getMapWorld().getNations().stream().allMatch(t-> t.getIdOwner()== idPlayerMaybeWinner))
                winFunction();
        }
        return res;
    }


    public boolean movementOfTroopsAtTheEnd(Nation start, Nation end, int idPlayer, int numOfTroops)
    {
        if(start.equals(end)) return false;
        if(! Checks.isNationReachable(start, end, idPlayer)) return false;
        if(numOfTroops<0) return false;
        if(numOfTroops > start.getTroops()-1) return false;

        start.removeTroops(numOfTroops);
        end.addTroops(numOfTroops);

        return true;
    }



    private void aPlayerDied(Player p)
    {
        p.setAlive(false);
        if(p.getTerritoryCardsOwned().size()> 0 )
        {
            for(TerritoryCard t: p.getTerritoryCardsOwned())
            {
                currentGame.getPlayers().get(currentPlayerActing).addTerritoryCards(t);
            }
            p.getTerritoryCardsOwned().clear();
            turnPhases.put(currentPlayerActing, PhaseTurn.fortify);
        }
    }

    private void winFunction()
    {
        setWinner(true);
        gamePhase=GamePhase.ending;
        //Todo : decide what to do and if something else needed
    }





    public void setWinner(boolean winner) {
        this.winner = winner;
    }
    public boolean isWinner() {
        return winner;
    }
    public SetupController getSetupController() {
        return setupController;
    }
    public ActionController getActionController() {
        return actionController;
    }
    public CurrentGame getCurrentGame() {
        return currentGame;
    }
    public GamePhase getGamePhase() {return gamePhase;}
    public PhaseTurn getPhase(int id){
        return turnPhases.get(id);
    }
    public HashMap<Integer, PhaseTurn> getPhases() {
        return turnPhases;
    }
    public int getCurrentPlayerActing() {
        return currentPlayerActing;
    }


    public int getModeOfWinning() {
        return modeOfWinning;
    }
    public void changeModeOfWinning(int typeOfWinning) {
        this.modeOfWinning = typeOfWinning;
    }
}
