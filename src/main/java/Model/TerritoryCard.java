package Model;

public class TerritoryCard {
    private final String nameTerritory;
    private final NationsName nationsName;
    private int typeOfTroop; //0,soldier; 1,cavalry; 2,artillery, 3 jolly;
    private final Nation nation;

    public TerritoryCard(String nameTerritory, int typeOfTroop , Nation nation, NationsName nationsName){
        this.nameTerritory = nameTerritory;
        this.typeOfTroop = typeOfTroop;
        this.nation = nation;
        this.nationsName=nationsName;
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
