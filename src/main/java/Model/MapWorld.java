package Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class MapWorld {
    @JsonBackReference
    private HashMap<String, Nation> nationsGlobalList;
    private HashMap<String,Continents> continents;

    /**
     * Constructor of the MapWorld
     * It first populate the continents and map of all the nations using the enumeration
     * Then it adds the neighborhoods
     */
    public MapWorld() {
        this.nationsGlobalList = new HashMap<>();
        this.continents=new HashMap<>();

        addNationToMapAndContinents("northAmerica",5,0,8);
        addNationToMapAndContinents("southAmerica",2,9,12);
        addNationToMapAndContinents("europe",5,13,19);
        addNationToMapAndContinents("africa",3,20,25);
        addNationToMapAndContinents("asia",7,26,37);
        addNationToMapAndContinents("australia",2,38,41);

        //addNeighbor(NationsName.WestEurope,new HashSet<>(Arrays.asList(NationsName.Ukrain, NationsName.Iceland)));
        //addNeighbor(NationsName.WestEurope,new HashSet<>(Arrays.asList(NationsName.Ukrain, NationsName.Iceland)));
        //alternative to manual initialization
        addNeighWithJson();
    }

    /**
     * First fetch all the nations that i will need using a loop that initializes a hashset with the list
     * Insert the continent if not already initialized
     * Add the nations (of this continents) in the @param nationsGlobalList
     * Add also those nations only to the specified continent
     * @param nameContinent the string with the name of the continents
     * @param troopsBonus   the number of soldiers to be added if you have all the continents
     * @param startInd      the start index of the enumeration of the current continent
     * @param stopInd      the stop index of the enumeration of the current continent (included)
     */
    private void addNationToMapAndContinents(String nameContinent, int troopsBonus, int startInd,int stopInd) {
        HashSet<NationsName> nationToAdd=new HashSet<>();
        for(int i=startInd;i <= stopInd;i++){
            nationToAdd.add(NationsName.values()[i]);
        }

        continents.putIfAbsent(nameContinent, new Continents(troopsBonus));

        //add to the countries
        for(NationsName nationToBeAdded: nationToAdd)
            nationsGlobalList.putIfAbsent(nationToBeAdded.toString(), new Nation(nationToBeAdded));

        //add to the continents
        for (NationsName nationToBeAdded : nationToAdd) {
            Nation nads = nationsGlobalList.entrySet().stream()
                    .filter(t -> t.getKey().equals(nationToBeAdded.toString()))
                    .findFirst().stream().map(t -> t.getValue()).findFirst().orElse(null);
            continents.get(nameContinent).addNations(nads);
        }
    }

    /**
     * Using a json this function intializes the neighbor of all the nations iterating on all the enumeration values
     */
    public void addNeighWithJson()
    {
        String fileJson = "./src/main/resources/nationJson.json";
        JsonNode rootNode=null;

        Path fileName = Paths.get(fileJson);
        try {
            rootNode = new ObjectMapper().readTree(Files.readString(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(NationsName name: NationsName.values())
        {
            JsonNode nationNode=rootNode.get(name.toString());
            for (JsonNode str: nationNode)
            {
                NationsName temp= NationsName.valueOf(str.textValue());
                this.getNation(name).addNeighbor(this.getNation( temp ));
            }
        }
    }





    public void fillMapWithSoldiers(int numPlayers, ArrayList<TerritoryCard> territoryCardArrayList) {
        int soldiersToUse=0;
        switch (numPlayers)
        {
            case 3: soldiersToUse=35;break;
            case 4: soldiersToUse=30;break;
            case 5: soldiersToUse=25;break;
            case 6: soldiersToUse=20;break;
        }
        ArrayList<TerritoryCard> deckForSplit= (ArrayList<TerritoryCard>) territoryCardArrayList.clone();
        int countPlayerID=0;
        HashMap soldiersToDeploy=new HashMap<Integer,Integer>();
        for(int i=0; i<numPlayers;i++) {soldiersToDeploy.put(i,soldiersToUse);}
        Collections.shuffle(deckForSplit);
        int numOfTerritoriesToSplit=deckForSplit.size();
        int currentPlayerTakingTroops;

        for(int i=0;i<numOfTerritoriesToSplit;i++)
        {
            TerritoryCard terExtrac=deckForSplit.remove(0);
            currentPlayerTakingTroops=countPlayerID% numPlayers;
            //devo piazzare su questo territorio 1 truppa
            terExtrac.getNation().setIdOwner(currentPlayerTakingTroops);
            terExtrac.getNation().addTroops(1);
            soldiersToDeploy.put(currentPlayerTakingTroops, (Integer) soldiersToDeploy.get(currentPlayerTakingTroops) -1);
            countPlayerID++;
        }
        //now I have to add the remaining troops
        Random numRandom=new Random();

        for(int i=0;i<numPlayers;i++)
        {
            while((Integer) soldiersToDeploy.get(i) !=0)
            {
                for(Nation nation: this.getNations())
                {
                    if(nation.getIdOwner()==i)
                    {
                        int bound=((Integer) soldiersToDeploy.get(nation.getIdOwner()))/4;
                        bound= bound>0 ? bound: 1;
                        int rand=numRandom.nextInt( bound )+1;
                        if(rand<= (Integer) soldiersToDeploy.get(i))
                        {
                            soldiersToDeploy.put(i, (Integer)soldiersToDeploy.get(i) - rand);
                            nation.addTroops(rand);
                        }
                        else if( (Integer) soldiersToDeploy.get(i) >=1)
                        {
                            soldiersToDeploy.put(i, (Integer)soldiersToDeploy.get(i) - 1);
                            nation.addTroops(1);
                        }
                        //System.out.println("Nation "+nation.getName()+" of player "+ nation.getIdOwner()+" has "+ nation.getTroops()+" troops");
                    }
                }
            }
        }
    }



    public List<Nation> getNations()
    {
        return nationsGlobalList.entrySet().stream().map(t-> t.getValue()).collect(Collectors.toList());
    }
    public Nation getNation(NationsName name)
    {
        return nationsGlobalList.get(name.toString());
    }

    public HashMap<String, Continents> getContinents() {
        return continents;
    }
}
