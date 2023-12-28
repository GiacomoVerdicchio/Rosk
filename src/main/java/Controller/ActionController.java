package Controller;

import Model.*;

import java.util.*;

public class ActionController {
    private CurrentGame currentGame;
    private HashMap<String,Integer> troopsBonus;
    private int troopsYetToBeDeployed=0;


    public ActionController(CurrentGame currentGame) {
        this.currentGame = currentGame;
        this.troopsYetToBeDeployed=0;
        this.troopsBonus=new HashMap<>();

        troopsBonus.putIfAbsent("infantrySet",4);
        troopsBonus.putIfAbsent("cavalrySet",6);
        troopsBonus.putIfAbsent("artillerySet",8);
        troopsBonus.putIfAbsent("tris",10);
        troopsBonus.putIfAbsent("trisWithCardOwned",12);
    }


    public void setOwnerToAllContinents() {
        int id=-1;
        for (String cont : currentGame.getMapWorld().getContinents().keySet()) {
            boolean sameProp = true;

            Nation ads[] = (Nation[]) currentGame.getMapWorld().getContinents().get(cont).getNations().stream().toArray(Nation[]::new);
            for (int i = 0; i < ads.length && sameProp; i++)
            {
                if(i==0)
                    id=ads[i].getIdOwner();
                else if (ads[i].getIdOwner() != id)
                    sameProp = false;
            }
            if (sameProp) {
                currentGame.getMapWorld().getContinents().get(cont).setIdOwner(id);
            }
            else {
                currentGame.getMapWorld().getContinents().get(cont).setIdOwner(-1);
            }
        }
    }

    public int calculateBasicTroopsAtFortify(int playerId)
    {
        setOwnerToAllContinents();

        int tot= (int) currentGame.getMapWorld().getNations().stream().filter(n->n.getIdOwner()==playerId).count();
        int count=tot/3;
        count= Math.max(count, 3);

        int numOfTroopsFromContin=0;
        for(String co: currentGame.getMapWorld().getContinents().keySet())
        {
            if(currentGame.getMapWorld().getContinents().get(co).getIdOwner() == playerId){
                numOfTroopsFromContin+=currentGame.getMapWorld().getContinents().get(co).getBonusTroops();
            }
        }
        return numOfTroopsFromContin+count;
    }

    public void setTroopsToBeStillDeployedWithoutTer(int idPlayer)
    {
        troopsYetToBeDeployed = calculateBasicTroopsAtFortify(idPlayer);
    }

    protected void useTerritoryCards(int index1, int index2, int index3, int idPlayer)
    {
        ArrayList <TerritoryCard> cards = new ArrayList<>();
        int troops=0;
        cards.add (currentGame.getPlayers().get(idPlayer).getTerritoryCardsOwned().get(index1));
        cards.add (currentGame.getPlayers().get(idPlayer).getTerritoryCardsOwned().get(index2));
        cards.add (currentGame.getPlayers().get(idPlayer).getTerritoryCardsOwned().get(index3));
        int isTheTypeInsert[]=new int[]{0,0,0,0};
        //count the number of same types
        for(int i=0;i<3;i++) {
            switch (cards.get(i).getTypeOfTroop()) {
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
        if(isTheTypeInsert[0]==3 || (isTheTypeInsert[0]==2 && isTheTypeInsert[3]==1))
            troops= troopsBonus.get("infantrySet");
        else if(isTheTypeInsert[1]==3 || (isTheTypeInsert[1]==2 && isTheTypeInsert[3]==1))
            troops= troopsBonus.get("cavalrySet");
        else if(isTheTypeInsert[2]==3 || (isTheTypeInsert[2]==2 && isTheTypeInsert[3]==1))
            troops= troopsBonus.get("artillerySet");
        else if(isTheTypeInsert[0]==1  && isTheTypeInsert[1]==1 && isTheTypeInsert[2]==1)
        {
            troops= troopsBonus.get("tris");
            if ( cards.stream().map(t->t.getNation().getIdOwner()).anyMatch(x-> x== idPlayer)  )
                troops= troopsBonus.get("trisWithCardOwned");
        }
        troopsYetToBeDeployed+= troops;
        TerritoryCard t1=currentGame.getPlayers().get(idPlayer).getTerritoryCardsOwned().get(index1);
        TerritoryCard t2=currentGame.getPlayers().get(idPlayer).getTerritoryCardsOwned().get(index2);
        TerritoryCard t3=currentGame.getPlayers().get(idPlayer).getTerritoryCardsOwned().get(index3);
        currentGame.getPlayers().get(idPlayer).removeTerritoryCards(t1);
        currentGame.getPlayers().get(idPlayer).removeTerritoryCards(t2);
        currentGame.getPlayers().get(idPlayer).removeTerritoryCards(t3);
        //return troops;
    }

    public boolean computeAttack(Nation start,Nation target, int attackTroops,int  defendTroops)
    {
        ArrayList<Integer> attack=new ArrayList<>();
        ArrayList<Integer> defend=new ArrayList<>();
        attack.clear();
        defend.clear();
        Random rand=new Random();
        for(int i=0;i<attackTroops;i++)
        {
            rand=new Random();
            attack.add(rand.nextInt(6)+1);
        }
        rand=new Random();
        for(int i=0;i<defendTroops;i++)
        {
            rand=new Random();
            defend.add(rand.nextInt(6)+1);
        }
        Collections.sort(attack, Comparator.reverseOrder());
        Collections.sort(defend, Comparator.reverseOrder());

        int pointerDice=0;

        int lostTroopsAttack=0;
        int lostTroopsDefend=0;
        while(attack.size() >pointerDice   &&    defend.size() >pointerDice){
            if(attack.get(pointerDice)> defend.get(pointerDice))
            {
                lostTroopsDefend++;
            }
            else
            {
                lostTroopsAttack++;
            }
            pointerDice++;
        }
        if(target.getTroops()<=lostTroopsDefend) //win of the attacker -> I have to move the troops so return true
        {
            target.removeAllTroops();
            start.removeTroops(lostTroopsAttack);

            int id=start.getIdOwner();
            target.setIdOwner(id);
            setOwnerToAllContinents();

            TerritoryCard t=currentGame.drawCardFromRemaingPile();
            currentGame.getPlayers().stream().filter(p->p.getIdPlayer()==id).findFirst().get().addTerritoryCards(t);
            return true;
        }
        else {      //win of the defender
            start.removeTroops(lostTroopsAttack);
            target.removeTroops(lostTroopsDefend);
        }
        return false;
    }










    public void removeTroopsYetToBeDeployed(int numTroops){
        troopsYetToBeDeployed-=numTroops;
    }
    public int getTroopsYetToBeDeployed() {
        return troopsYetToBeDeployed;
    }
}

