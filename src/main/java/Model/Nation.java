package Model;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonManagedReference;

public class Nation {
    private NationsName nationName;
    private int troops;
    @JsonManagedReference
    private Set neighbors;
    private int idOwner;

    @JsonCreator
    public Nation(NationsName name) {
        this.nationName = name;
        troops=0;
        neighbors=new HashSet();
        idOwner=-1;
    }

    public void addNeighbor(Nation nation)
    {
        neighbors.add(nation.nationName);
    }

    public int getIdOwner() {
        return idOwner;
    }
    public void setIdOwner(int idOwner) {
        this.idOwner = idOwner;
    }
    public Set<NationsName> getNeighbors(){return neighbors;}
    public NationsName getNationName() {
        return nationName;
    }
    public int getTroops() {
        return troops;
    }
    public void addTroops(int troopsToAdd) {
        this.troops+=troopsToAdd;
    }
    public void removeAllTroops() {
        this.troops=0;
    }
    public void removeTroops(int troopsToRemove) {troops-=troopsToRemove;}
}
