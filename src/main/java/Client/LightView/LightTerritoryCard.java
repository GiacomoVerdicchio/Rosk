package Client.LightView;

import Model.Nation;
import Model.NationsName;
import Observer.Observable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LightTerritoryCard extends Observable {
    private final String nameTerritory;
    private final NationsName nationsName;
    private final int typeOfTroop; //0,soldier; 1,cavalry; 2,artillery, 3 jolly;
    private final Nation nation;

    public LightTerritoryCard(@JsonProperty("nameTerritory") String nameTerritory,
                              @JsonProperty("typeOfTroop") int typeOfTroop ,
                              @JsonProperty("nation") Nation nation,
                              @JsonProperty("nationsName") NationsName nationsName){
        this.nameTerritory = nameTerritory;
        this.typeOfTroop = typeOfTroop;
        this.nation = nation;
        this.nationsName=nationsName;
    }



    public void updateTerritoryCard(LightTerritoryCard newLight) {
        notifyLight(this);
    }

    public String getNameTerritory() {
        return nameTerritory;
    }
    public int getTypeOfTroop() {
        return typeOfTroop;
    }
    public Nation getNation() {
        return nation;
    }
    public NationsName getNationsName() {
        return nationsName;
    }
}
