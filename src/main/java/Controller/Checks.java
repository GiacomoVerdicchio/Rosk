package Controller;

import Model.*;

import java.util.*;

public class Checks {

    public static boolean checkMaxNumberOfPlayers(int ready)
    {
        if(ready>6 || ready<3) return false;
        return true;
    }

    public static int getIdOfPlayer(String name, ArrayList<Player> players)
    {
        return players.stream().filter(t->t.getName().equals(name)).map(t->t.getIdPlayer()).findFirst().orElse(-1);
    }
    public static String getNameOfPlayerById(int id, ArrayList<Player> players)
    {
        return players.stream().filter(t->t.getIdPlayer()==id).map(t->t.getName()).findFirst().orElse(null);
    }
    public static Player getPlayerById(int id, ArrayList<Player> players)
    {
        return players.stream().filter(t->t.getIdPlayer()==id).findFirst().orElseThrow();
    }


    public static boolean checkForAddTroopsToFortify(Nation nation, int partialTroopsToDeploy,int idOwner, int totalTroopsToDeploy)
    {
        if(nation.getIdOwner() != idOwner) return false;
        if(partialTroopsToDeploy < 0 || partialTroopsToDeploy > totalTroopsToDeploy) return false;

        return true;
    }

    public static boolean checkForNoMoreThan5CardInRemainingDeck(ArrayList<TerritoryCard> deck)
    {
        if (deck.size()>= 5) return false;
        return true;
    }


    public static boolean checkForMaxTroopsNumber(Nation start,Nation target, int attackTroops,int  defendTroops)
    {
        if(start.getTroops()>= attackTroops && target.getTroops()>=defendTroops)
            return true;
        return false;
    }

    public static boolean isNationReachable(Nation start, Nation target, int idPlayer){
        Set<Nation> visited = new HashSet<>();
        Queue<Nation> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Nation current = queue.poll();

            if (current == target) {
                return true; // Target is reachable
            }
            for (Nation neighbor : current.getNeighbor()) {
                if (!visited.contains(neighbor) && idPlayer==neighbor.getIdOwner()) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return false; // Target is not reachable
    }



    public static boolean checkForAttack(Nation start, Nation target, int troopsAttack)
    {
        int idStart=start.getIdOwner();
        int idEnd=target.getIdOwner();

        if(troopsAttack<=1) return false;
        if(idStart==idEnd) return false;

        return start.getNeighbor()
                .stream().anyMatch(
                        t->t.getNationName().equals(target.getNationName()      ));
    }
    public static boolean checkForAttackAndDefend(Nation start, Nation target, int troopsAttack,int troopsDefend, GamePhase gamePhase , HashMap<Integer,PhaseTurn> phases)
    {
        //Checks of the phase of game and turn
        if(! gamePhase.equals(GamePhase.loop)) return false;
        if( !phases.get( start.getIdOwner() ).equals(PhaseTurn.attack))
            return false;
        if(target.getIdOwner() == start.getIdOwner()) return false;

        if(start.getTroops()< troopsAttack || target.getTroops()< troopsDefend) return false;
        if(troopsDefend<=0) return false;
        return Checks.checkForAttack(start,target,troopsAttack);
    }



    public static boolean checkAreCardsPassedValid(int index1, int index2, int index3, int idPlayer, ArrayList<Player> players)
    {
        if(index1 < 0 || index2 < 0 || index3 < 0) return false;
        if(index1 > 4 || index2 > 4 || index3 > 4) return false;
        if(index1==index2 || index2==index3 || index1==index3) return false;
        if(idPlayer<0 || idPlayer> players.size()) return false;

        ArrayList<TerritoryCard> territoriesArray= players.stream().filter(p -> p.getIdPlayer()==idPlayer).findFirst().orElse(null).getTerritoryCardsOwned();
        ArrayList<Integer> indexArray=new ArrayList<>(Arrays.asList(index1,index2,index3));

        int isTheTypeInsert[]=new int[]{0,0,0,0};

        //count the number of same types
        for(int i=0;i<3;i++) {
            switch (territoriesArray.get(indexArray.get(i)).getTypeOfTroop()) {
                case 0:
                    isTheTypeInsert[0] ++;
                    break;
                case 1:
                    isTheTypeInsert[1] ++;
                    break;
                case 2:
                    isTheTypeInsert[2] ++;
                    break;
                case 3:
                    isTheTypeInsert[3] ++;
                    break;
            }
        }
        //2 jolly not allowed
        if(isTheTypeInsert[3]==2)return false;


        if(isTheTypeInsert[0]==3 || isTheTypeInsert[1]==3 || isTheTypeInsert[2]==3 ) return true;
        if(isTheTypeInsert[0]==1 && isTheTypeInsert[1]==1 && isTheTypeInsert[2]==1 ) return true;

        if((isTheTypeInsert[0]==1 && isTheTypeInsert[1]==1 && isTheTypeInsert[3]==1)  ||
                (isTheTypeInsert[0]==1 && isTheTypeInsert[2]==1 && isTheTypeInsert[3]==1)  ||
                (isTheTypeInsert[1]==1 && isTheTypeInsert[2]==1 && isTheTypeInsert[3]==1)) return true;

        if((isTheTypeInsert[0]==2 && isTheTypeInsert[3]==1) ||
                (isTheTypeInsert[1]==2 && isTheTypeInsert[3]==1) ||
                (isTheTypeInsert[2]==2 && isTheTypeInsert[3]==1) )return true;
        return false;
    }



}
