package Client.LightView;

import Model.TerritoryCard;
import Observer.Observable;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class LightPlayer extends Observable {
    private String name;
    private ArrayList<LightTerritoryCard> lightTerritoryCards;
    private int idPlayer;
    private boolean alive;

    public LightPlayer( @JsonProperty("name") String name,
                        @JsonProperty("lightTerritoryCards") ArrayList<LightTerritoryCard> lightTerritoryCards,
                        @JsonProperty("idPlayer") int idPlayer,
                        @JsonProperty("alive") Boolean alive) {
        this.name = name;
        this.idPlayer=idPlayer;
        this.lightTerritoryCards = lightTerritoryCards;
        this.alive = alive;
    }

    public void updatePlayer(LightPlayer newLight) {
        this.name = newLight.getName();
        this.lightTerritoryCards.clear();
        for(int i = 0; i < lightTerritoryCards.size(); i++)
        {
            lightTerritoryCards.get(i).updateTerritoryCard(newLight.getLightTerritoryCards().get(i));
        }
        this.idPlayer=newLight.getIdPlayer();
        this.alive = newLight.isAlive();
        notifyLight(this);
    }

    public String getName() {
        return name;
    }
    public ArrayList<LightTerritoryCard> getLightTerritoryCards() {
        return lightTerritoryCards;
    }
    public int getIdPlayer() {
        return idPlayer;
    }
    public boolean isAlive() {
        return alive;
    }
}