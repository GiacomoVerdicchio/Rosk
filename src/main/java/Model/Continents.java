package Model;

import java.util.HashSet;

public class Continents {
    private HashSet nations;
    private int bonusTroops;
    private int idOwner;

    public Continents(int bonusTroops) {
        nations=new HashSet<Nation>();
        this.bonusTroops = bonusTroops;
        idOwner=-1;
    }

    public void addNations(Nation nation)
    {
        nations.add(nation);
    }
    public void setIdOwner(int idOwner) {
        this.idOwner = idOwner;
    }

    public int getBonusTroops() {
        return bonusTroops;
    }
    public HashSet getNations() {
        return nations;
    }
    public int getIdOwner() {
        return idOwner;
    }
}
