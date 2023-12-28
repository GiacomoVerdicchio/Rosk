import Controller.*;
import Model.Nation;
import Model.NationsName;
import Model.Player;
import Model.TerritoryCard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestActionController {

    Controller controller;

    @Test
    protected void basicControllerInitialized(){
        controller = new Controller();
        controller.getSetupController().addPlayer("Io");
        controller.getSetupController().addPlayer("Chiara");
        controller.getSetupController().addPlayer("Ale");
        controller.getSetupController().addPlayer("Alessia");

        try {
            controller.getSetupController().addReady("Io");
            controller.getSetupController().addReady("Alessia");
            controller.getSetupController().addReady("Ale");
            controller.getSetupController().addReady("Chiara");

            controller.generalSetupAfterReady();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testOfPhases()
    {
        basicControllerInitialized();
        assertEquals(PhaseTurn.fortify,controller.getPhase(0));//io
        assertEquals(PhaseTurn.wait,controller.getPhase(1));//chiara
        assertEquals(PhaseTurn.wait,controller.getPhase(2));
        assertEquals(PhaseTurn.wait,controller.getPhase(3));

        //test updates of the phases

        //false because I have to deploy the troops before
        assertFalse(controller.updateTurnPhase(0));

        int numOfTroopToDep=controller.getActionController().getTroopsYetToBeDeployed();
        NationsName n=controller.getCurrentGame().getMapWorld().getNations().stream().filter(x->x.getIdOwner()==0).findFirst().get().getNationName();
        controller.addTroopsToFortify( n, numOfTroopToDep, 0);
        assertTrue(controller.updateTurnPhase(0));

        assertEquals(PhaseTurn.attack,controller.getPhase(0));
        assertTrue(controller.updateTurnPhase(0));
        assertEquals(PhaseTurn.moveAtTheEnd,controller.getPhase(0));
        assertTrue(controller.updateTurnPhase(0));

        assertEquals(1, controller.getCurrentPlayerActing());
        assertEquals(PhaseTurn.fortify, controller.getPhase(1));

        //preparation to the Checks
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan).setIdOwner(1);

        numOfTroopToDep=controller.getActionController().getTroopsYetToBeDeployed();
        n=controller.getCurrentGame().getMapWorld().getNations().stream().filter(x->x.getIdOwner()==1).findFirst().get().getNationName();
        controller.addTroopsToFortify( n, numOfTroopToDep, 1);

        assertTrue(controller.updateTurnPhase(1));
        assertEquals(PhaseTurn.attack,controller.getPhase(1));

    }


    @Test
    public void testCheckForAttack()
    {
        testOfPhases();
        //CHECK ONLY FOR ATTACK
        assertTrue(Checks.checkForAttack(
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia),
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan),2));

        //not neighboring
        assertFalse(Checks.checkForAttack(
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia),
                controller.getCurrentGame().getMapWorld().getNation(NationsName.NorthAfrica),2));

        //same id
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan).setIdOwner(0);
        assertFalse(Checks.checkForAttack(
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia),
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan),2));

        //troops<=1
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan).setIdOwner(1);
        assertFalse(Checks.checkForAttack(
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia),
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan),1));


    }

    @Test
    public void testCheckAttackAndDefend()
    {
        testOfPhases();

        assertEquals(GamePhase.loop, controller.getGamePhase());
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia).setIdOwner(Checks.getIdOfPlayer("Chiara",controller.getCurrentGame().getPlayers()));
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan).setIdOwner(Checks.getIdOfPlayer("Io",controller.getCurrentGame().getPlayers()));

        //NOW CHECK ALSO FOR THE DEFENSE (since is more difficult)
        assertTrue(Checks.checkForAttackAndDefend(
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia),
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan),
                2,2, controller.getGamePhase() , controller.getPhases() ));

        //not neighboring
        assertFalse(Checks.checkForAttack(
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia),
                controller.getCurrentGame().getMapWorld().getNation(NationsName.NorthAfrica),2));

        //same id
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan).setIdOwner(Checks.getIdOfPlayer("Chiara",controller.getCurrentGame().getPlayers()));
        assertFalse(Checks.checkForAttack(
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia),
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan),2));

        //troops<=1
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan).setIdOwner(Checks.getIdOfPlayer("Io",controller.getCurrentGame().getPlayers()));
        assertFalse(Checks.checkForAttack(
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia),
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan),1));
    }



    @Test
    public void testCompute()
    {
        testOfPhases();
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan).setIdOwner(Checks.getIdOfPlayer("Io",controller.getCurrentGame().getPlayers()));
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia).setIdOwner(Checks.getIdOfPlayer("Chiara",controller.getCurrentGame().getPlayers()));
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan).removeAllTroops();
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia).removeAllTroops();
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan).addTroops(1);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia).addTroops(3);


        boolean res= controller.attack(     controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia),
                                controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan)  , 3 ,1);

        if(res ) {
            TerritoryCard tExtracted;
            String player = null;
            assertEquals(43, controller.getCurrentGame().getTerritoriesRemainingDeck().size());

            for(TerritoryCard t: controller.getCurrentGame().getTerritoriesDeck())
            {
                if(! controller.getCurrentGame().getTerritoriesRemainingDeck().stream().map(TerritoryCard::getNameTerritory)
                        .toList().contains(t.getNameTerritory()))
                {
                    tExtracted=t;
                    for(Player p : controller.getCurrentGame().getPlayers())
                    {
                        if(p.getTerritoryCardsOwned().contains(tExtracted))
                        {
                            player=p.getName();
                        }
                    }
                }
            }
            if(player==null)
                System.out.println("AIAA ( testActionController");
        }

        controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan).removeAllTroops();
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia).removeAllTroops();
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan).addTroops(3);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia).addTroops(1);
        boolean res2= controller.getActionController().computeAttack(     controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia),
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan)  , 1 ,3);

        if(!res2 && res)
            assertEquals(43, controller.getCurrentGame().getTerritoriesRemainingDeck().size());
    }

    @Test
    public void testCheckForContinentOwning()
    {
        testOfPhases();

        controller.getCurrentGame().getMapWorld().getNation(NationsName.Indonesia).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.EastAustralia).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.WestAustralia).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.NewGuinea).setIdOwner(0);


        controller.getActionController().setOwnerToAllContinents();

        assertEquals(0, controller.getCurrentGame().getMapWorld().getContinents().get("australia").getIdOwner());

    }

    @Test
    public void testSetOwnerPlayer()
    {
        testCheckForContinentOwning();
        Player p=controller.getCurrentGame().getPlayers().get(0);
        controller.getCurrentGame().getMapWorld().getNations().stream().forEach(x->x.setIdOwner(1));


        controller.getCurrentGame().getMapWorld().getNation(NationsName.Indonesia).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.EastAustralia).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.WestAustralia).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.NewGuinea).setIdOwner(0);

        assertEquals(5, controller.getActionController().calculateBasicTroopsAtFortify(p.getIdPlayer()));


        controller.getCurrentGame().getMapWorld().getNation(NationsName.NorthTerritory).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.NorthAfrica).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Brazil).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.China).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Yakutsk).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.India).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Ukraine).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan).setIdOwner(0);

        assertEquals(6, controller.getActionController().calculateBasicTroopsAtFortify(p.getIdPlayer()));


        controller.getCurrentGame().getMapWorld().getNation(NationsName.Alberta).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Iceland).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Egypt).setIdOwner(0);

        assertEquals(7, controller.getActionController().calculateBasicTroopsAtFortify(p.getIdPlayer()));


        controller.getCurrentGame().getMapWorld().getNation(NationsName.Peru).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Argentina).setIdOwner(0);

        assertEquals(10, controller.getActionController().calculateBasicTroopsAtFortify(p.getIdPlayer()));
    }

    @Test
    public void testSetAllOwners()
    {
        testCheckForContinentOwning();
        Player p=controller.getCurrentGame().getPlayers().get(0);
        controller.getCurrentGame().getMapWorld().getNations().stream().forEach(x->x.setIdOwner(1));
        controller.getActionController().setOwnerToAllContinents();

        for (String cont : controller.getCurrentGame().getMapWorld().getContinents().keySet()) {
            assertEquals(1,controller.getCurrentGame().getMapWorld().getContinents().get(cont).getIdOwner());
        }

        //now 0 claims australia
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Indonesia).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.EastAustralia).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.WestAustralia).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.NewGuinea).setIdOwner(0);

        controller.getActionController().setOwnerToAllContinents();
        for (String cont : controller.getCurrentGame().getMapWorld().getContinents().keySet()) {
            if(cont.equals("australia"))
                assertEquals(0,controller.getCurrentGame().getMapWorld().getContinents().get(cont).getIdOwner());
            else
                assertEquals(1,controller.getCurrentGame().getMapWorld().getContinents().get(cont).getIdOwner());
        }
    }


    @Test
    public void testCardsForBonus()
    {
        basicControllerInitialized();
        testOfPhases();
        Player p=controller.getCurrentGame().getPlayers().get(0);

        //too high or low value    or equals
        assertFalse(Checks.checkAreCardsPassedValid(5,4,2,p.getIdPlayer(), controller.getCurrentGame().getPlayers()));
        assertFalse(Checks.checkAreCardsPassedValid(-123,4,2,p.getIdPlayer(), controller.getCurrentGame().getPlayers()));
        assertFalse(Checks.checkAreCardsPassedValid(1,4,4,p.getIdPlayer(), controller.getCurrentGame().getPlayers()));
        assertFalse(Checks.checkAreCardsPassedValid(1,3,2,-1, controller.getCurrentGame().getPlayers()));
        assertFalse(Checks.checkAreCardsPassedValid(1,3,2,123, controller.getCurrentGame().getPlayers()));

        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertTrue(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        assertEquals(0,p.getTerritoryCardsOwned().size());

        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertTrue(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        assertEquals(0,p.getTerritoryCardsOwned().size());

        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertTrue(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        assertEquals(0,p.getTerritoryCardsOwned().size());

        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Jolly",3, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertTrue(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        assertEquals(0,p.getTerritoryCardsOwned().size());

        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Jolly",3, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertTrue(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        assertEquals(0,p.getTerritoryCardsOwned().size());

        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Jolly",3, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertTrue(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        assertEquals(0,p.getTerritoryCardsOwned().size());

        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Jolly",3, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertTrue(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        assertEquals(0,p.getTerritoryCardsOwned().size());

        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Jolly",3, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertTrue(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        assertEquals(0,p.getTerritoryCardsOwned().size());

        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Jolly",3, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertTrue(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        assertEquals(0,p.getTerritoryCardsOwned().size());

        //FALSE

        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertFalse(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        assertEquals(0,p.getTerritoryCardsOwned().size());

        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertFalse(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        assertEquals(0,p.getTerritoryCardsOwned().size());

        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertFalse(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        assertEquals(0,p.getTerritoryCardsOwned().size());

        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertFalse(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        assertEquals(0,p.getTerritoryCardsOwned().size());

        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertFalse(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        assertEquals(0,p.getTerritoryCardsOwned().size());

        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertFalse(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        p.getTerritoryCardsOwned().remove(0);
        assertEquals(0,p.getTerritoryCardsOwned().size());

    }

    /**Here all the assert are commented because I changed the structure and the function doesn't no more provide the number of
     * troops inserted using the cards, but you can modified it if needed and reintroduce it **/
    @Test
    public void testFortifyTroops()
    {
        basicControllerInitialized();
        testOfPhases();
        Player p=controller.getCurrentGame().getPlayers().get(0);

        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertTrue(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        //assertEquals(4,controller.getActionController().useTerritoryCards(0,1,2,0));

        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertTrue(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        //assertEquals(6,controller.getActionController().useTerritoryCards(0,1,2,0));

        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Artillery",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertTrue(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        //assertEquals(8,controller.getActionController().useTerritoryCards(0,1,2,0));

        Nation mong= controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia);
        p.addTerritoryCards(new TerritoryCard("Infantry",0, mong,NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Cavalry",1, mong,NationsName.Mongolia));
        p.addTerritoryCards(new TerritoryCard("Artillery",2, mong,NationsName.Mongolia));
        assertTrue(Checks.checkAreCardsPassedValid(0,1,2,0, controller.getCurrentGame().getPlayers()));
        /*
        if(controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia).getIdOwner() == p.getIdPlayer())
            assertEquals(12,controller.getActionController().useTerritoryCards(0,1,2,0));
        else {
            controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia).setIdOwner(p.getIdPlayer());
            assertEquals(10,controller.getActionController().useTerritoryCards(0,1,2,0));
        }*/
    }


    @Test
    public void test()
    {
        basicControllerInitialized();
        Player p=controller.getCurrentGame().getPlayers().get(0);



        assertEquals(PhaseTurn.fortify,controller.getPhase(0));//io
        assertEquals(PhaseTurn.wait,controller.getPhase(1));//chiara
        assertEquals(PhaseTurn.wait,controller.getPhase(2));
        assertEquals(PhaseTurn.wait,controller.getPhase(3));

        controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan).setIdOwner(1);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Greenland).setIdOwner(1);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Scandinavia).setIdOwner(1);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.GreatBritain).setIdOwner(1);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.India).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Kamchatka).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Iceland).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Siam).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Siam).addTroops(3);
        assertTrue(controller.addTroopsToFortify( NationsName.Kamchatka, controller.getActionController().getTroopsYetToBeDeployed(), 0 ));
        assertTrue(controller.updateTurnPhase(0));

        assertTrue(controller.updateTurnPhase(0));
        assertTrue(controller.movementOfTroopsAtTheEnd(controller.getCurrentGame().getMapWorld().getNation(NationsName.Siam),
                    controller.getCurrentGame().getMapWorld().getNation(NationsName.India), 0,2 ));
        assertFalse(controller.movementOfTroopsAtTheEnd(controller.getCurrentGame().getMapWorld().getNation(NationsName.Siam),
                controller.getCurrentGame().getMapWorld().getNation(NationsName.India), 0,10 ));
        assertFalse(controller.movementOfTroopsAtTheEnd(controller.getCurrentGame().getMapWorld().getNation(NationsName.Siam),
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Siam), 0,1 ));
        assertFalse(controller.movementOfTroopsAtTheEnd(controller.getCurrentGame().getMapWorld().getNation(NationsName.Siam),
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Iceland), 0,1 ));
        assertFalse(controller.movementOfTroopsAtTheEnd(controller.getCurrentGame().getMapWorld().getNation(NationsName.Siam),
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan), 0,1 ));

        assertTrue(controller.updateTurnPhase(0));
        assertEquals(PhaseTurn.fortify,controller.getPhase(1));
    }

}
