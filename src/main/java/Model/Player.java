package Model;

import java.util.ArrayList;

public class Player {

    private String name;
    int idPlayer;//id used to identify players
    ObjectiveCards objectiveCards;
    ArrayList<TerritoryCard> territoryCardOwned;
    boolean alive;

    public Player(String name) {
        this.name = name;
        territoryCardOwned = new ArrayList<>();
        alive= true;
    }

    public void setIdPlayer(int idPlayer){this.idPlayer = idPlayer;}
    public int getIdPlayer() {
        return idPlayer;
    }

    public void addTerritoryCards(TerritoryCard territoryCard){
        territoryCardOwned.add(territoryCard);
    }
    public void removeTerritoryCards(TerritoryCard territoryCard){
        territoryCardOwned.remove(territoryCard);
    }

    public void addObjectiveCards(ObjectiveCards objectiveCards)
    {
        this.objectiveCards=objectiveCards;
    }
    public ObjectiveCards getObjectiveCards() {
        return objectiveCards;
    }

    public ArrayList<TerritoryCard> getTerritoryCardsOwned() {
        return territoryCardOwned;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isAlive() {
        return alive;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
