package Model;

import Observer.Observable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;



@JsonSerialize(using = SerializerCurrentGame.class)
public class CurrentGame extends Observable {
    private MapWorld mapWorld;
    private ArrayList<Player> players;
    private ArrayList<TerritoryCard> territoriesDeck;
    private ArrayList<TerritoryCard> territoriesRemainingDeck;
    private ArrayList<ObjectiveCards> objectiveCards;


    public CurrentGame() {

        players= new ArrayList<>();
        territoriesDeck = new ArrayList<>();
        territoriesRemainingDeck = new ArrayList<>();
        objectiveCards= new ArrayList<>();
        mapWorld =new MapWorld();

        //to be setted all the objectives and territories cards (calling a specific function that gives values)
        fillTerritoryCardsALL();
    }

    /**
     * Method used by the notifies to compile the json file with the currentGameState information
     * @return a string representing the json file
     */
    public String modelToJson()
    {
        String json = null;
        try {
            json = new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void fillTerritoryCardsALL()
    {
        String fileJson = "./src/main/resources/territoryCardsJSON.json";
        JsonNode rootNode;
        Path fileName = Paths.get(fileJson);
        try {
            rootNode = new ObjectMapper().readTree(Files.readString(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(NationsName name: NationsName.values())
        {
            JsonNode nationNode=rootNode.get(name.toString());
            territoriesDeck.add(new TerritoryCard(name.toString(),nationNode.asInt(), mapWorld.getNation(name) ,name ));
        }
    }

    public TerritoryCard drawCardFromRemaingPile() {
        if(territoriesRemainingDeck.size()==0)
            refillDeckTerritoriesRemaining();
        return territoriesRemainingDeck.remove(0);
    }

    public void refillDeckTerritoriesRemaining()
    {
        territoriesRemainingDeck=(ArrayList<TerritoryCard>) territoriesDeck.clone();
        Collections.shuffle(territoriesRemainingDeck);
    }



    public void addPlayer(String name)
    {
        players.add(new Player(name));
    }
    public void removePlayer(String name)
    {
        players.removeIf(player -> player.getName().equals(name));
    }
    public MapWorld getMapWorld() {
        return mapWorld;
    }
    public ArrayList<Player> getPlayers() {
        return players;
    }
    public ArrayList<TerritoryCard> getTerritoriesDeck() {
        return territoriesDeck;
    }
    public ArrayList<TerritoryCard> getTerritoriesRemainingDeck() {
        return territoriesRemainingDeck;
    }
    public ArrayList<ObjectiveCards> getObjectiveCards() {
        return objectiveCards;
    }
}
