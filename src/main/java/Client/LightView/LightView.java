package Client.LightView;

import Observer.Observable;
import Server.Answer.Action.ViewMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class LightView extends Observable {
    private Boolean firstUpdate = true;

    private LightMapWorld mapWorld;
    private ArrayList<LightPlayer> players; //will be modified since i cannot see all the info of other players
    private ArrayList<LightTerritoryCard> territoriesDeck;


    /**
     * The method parses the ViewMessage containing the JSON serialization of the game model
     * @param view the viewMessage containing the JSON
     */
    public void parse(ViewMessage view) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = view.getJsonView();
        LightView lv;
        lv = objectMapper.readValue(json, LightView.class);

        updateLightView(lv);
    }

    /**
     * Method updateLightView updates the client view and its objects, after the deserialization of the model JSON
     * @param newView the up-to-date view
     */
    public void updateLightView(LightView newView)
    {

        if(!firstUpdate)
        {
            for(int i = 0; i < players.size(); i++)
            {
                players.get(i).updatePlayer(newView.players.get(i));
            }
            for(int i=0; i< territoriesDeck.size(); i++)
            {
                territoriesDeck.get(i).updateTerritoryCard(newView.territoriesDeck.get(i));
            }
            mapWorld.updateMapWorld(newView.mapWorld);
        }
        else
        {
            this.mapWorld=newView.mapWorld;
            this.players=newView.players;
            this.territoriesDeck=newView.territoriesDeck;

            notifyLight(this);
            firstUpdate = false;
        }
    }


    public LightMapWorld getMapWorld() {
        return mapWorld;
    }
    public ArrayList<LightPlayer> getPlayers() {
        return players;
    }
    public ArrayList<LightTerritoryCard> getTerritoriesDeck() {
        return territoriesDeck;
    }
}
