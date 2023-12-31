package Client.LightView;

import Model.Continents;
import Model.Nation;
import Model.NationsName;
import Observer.Observable;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class LightMapWorld extends Observable {
    private HashMap<String, Nation> nationsGlobalList;
    private HashMap<String, Continents> continents;

    public LightMapWorld( @JsonProperty("nationsGlobalList") HashMap nationsGlobalList,
                        @JsonProperty("continents") HashMap continents) {
        this.nationsGlobalList=nationsGlobalList;
        this.continents = continents;
    }



    public void updateMapWorld(LightMapWorld newLight) {

        this.nationsGlobalList = newLight.getNationsGlobalList();
        this.continents=newLight.getContinents();
        notifyLight(this);
    }

    public List<Nation> getNations()
    {
        return nationsGlobalList.entrySet().stream().map(t-> t.getValue()).collect(Collectors.toList());
    }
    public HashMap<String, Nation> getNationsGlobalList() {
        return nationsGlobalList;
    }
    public HashMap<String, Continents> getContinents() {
        return continents;
    }

}
