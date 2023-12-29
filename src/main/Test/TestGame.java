import Controller.*;
import Model.Nation;
import Model.NationsName;
import Model.Player;
import Model.TerritoryCard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TestGame {
    private Controller controller;

    /**
     * All the world is of chiara, except for the southAmerica where there is still a nation of mine: Venezuela
     * So here Chiara attack me there and conquest it, I want to see of all the Checks works (turn,troops) and if also
     * the update of the continent work
     */
    @Test
    public void testSimpleGame()
    {
        //BASIC INITIALIZATION
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

        //TEST OF PHASE PART
        assertEquals(PhaseTurn.fortify,controller.getPhase(0));//io
        assertEquals(PhaseTurn.wait,controller.getPhase(1));//chiara
        assertEquals(PhaseTurn.wait,controller.getPhase(2));
        assertEquals(PhaseTurn.wait,controller.getPhase(3));
        assertFalse(controller.updateTurnPhase(3));
        assertFalse(controller.updateTurnPhase(0));

        //FORTIFY PART
        int idIo=Checks.getIdOfPlayer("Io",controller.getCurrentGame().getPlayers());
        int idChiara=Checks.getIdOfPlayer("Chiara",controller.getCurrentGame().getPlayers());
        //all the states are of player 3, except for venezuela of 0 and peru,brazil and argentina (and all the australia) of 1
        controller.getCurrentGame().getMapWorld().getNations().stream().forEach( n -> n.setIdOwner(2));

        controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela).setIdOwner(idIo);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Peru).setIdOwner(idChiara);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Brazil).setIdOwner(idChiara);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Argentina).setIdOwner(idChiara);

        controller.getCurrentGame().getMapWorld().getNation(NationsName.NewGuinea).setIdOwner(idChiara);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.WestAustralia).setIdOwner(idChiara);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.EastAustralia).setIdOwner(idChiara);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Indonesia).setIdOwner(idChiara);
                    //To be called only because I've modified the flow od the game to add those nation and check things
                    controller.getActionController().setOwnerToAllContinents();
                    controller.getActionController().setTroopsToBeStillDeployedWithoutTer(0);

        long nState=controller.getCurrentGame().getMapWorld().getNations().stream().map(Nation::getIdOwner).filter(x->x.equals(idChiara)).count();
        int troopsToAddAtTheBeginning=controller.getActionController().calculateBasicTroopsAtFortify(idIo);
        assertEquals(controller.getActionController().getTroopsYetToBeDeployed(), troopsToAddAtTheBeginning);
        assertEquals( 3,  troopsToAddAtTheBeginning);
        controller.addTroopsToFortify(NationsName.Venezuela, troopsToAddAtTheBeginning, 0);



        //WE have to go to kiara, so we will skip all the middle useless things (but i'll check the basic
        //  handling of the phases
        assertTrue(controller.updateTurnPhase(0));//go to attack
        assertTrue(controller.updateTurnPhase(0));//go to move
        assertTrue(controller.updateTurnPhase(0));//go to fortify for kyarao

        assertTrue(controller.getPhase(1).equals(PhaseTurn.fortify));
        assertTrue(controller.getPhase(0).equals(PhaseTurn.wait));
        assertFalse(controller.updateTurnPhase(1));
        troopsToAddAtTheBeginning=controller.getActionController().getTroopsYetToBeDeployed();
        controller.addTroopsToFortify(NationsName.WestAustralia, troopsToAddAtTheBeginning, 1);


        //let's go to kyarao attack phase
        assertTrue(controller.updateTurnPhase(1));

        controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela).removeAllTroops();
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Peru).removeAllTroops();
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela).addTroops(1);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Peru).addTroops(3);

        controller.getCurrentGame().getMapWorld().getNation(NationsName.Brazil).setIdOwner(idChiara);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Argentina).setIdOwner(idChiara);
        controller.getActionController().setOwnerToAllContinents();

        for(String  co: controller.getCurrentGame().getMapWorld().getContinents().keySet())
        {
            if(co.equals("southAmerica"))
                assertEquals(-1, controller.getCurrentGame().getMapWorld().getContinents().get(co).getIdOwner());
            else if(co.equals("australia"))
                assertEquals(idChiara, controller.getCurrentGame().getMapWorld().getContinents().get(co).getIdOwner());

        }

        //this have to fail for troops defend error
        assertFalse(Checks.checkForAttackAndDefend(
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Peru),
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela),
                3,20, controller.getGamePhase() , controller.getPhases() ));
        assertFalse(Checks.checkForAttackAndDefend(
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Peru),
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela),
                20,1, controller.getGamePhase() , controller.getPhases() ) );

        //put the real attack and defend troops
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela).removeAllTroops();
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Peru).removeAllTroops();
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela).addTroops(10);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Peru).addTroops(80);

        boolean exit=false;
        boolean win=false;
        while(!exit)
        //PUT 10 troops in defending
        {
            //assertTrue(controller.getActionController().checkForAttackAndDefend(
            //                    controller.getCurrentGame().getMapWorld().getNation(NationsName.Peru),
            //                    controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela),
            //                    3,3, controller.getPhase()) );
            if(! Checks.checkForAttackAndDefend(
                    controller.getCurrentGame().getMapWorld().getNation(NationsName.Peru),
                    controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela),
                    3,3, controller.getGamePhase() , controller.getPhases() ) )
            {
                System.out.println("Defender won, at last (testGame)");
                exit=true;
            }else
            {
                assertFalse(controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela).getTroops()<=0);
                exit= controller.getActionController().computeAttack(controller.getCurrentGame().getMapWorld().getNation(NationsName.Peru) ,
                    controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela),3,3);
                if(exit) win=true;
            }
        }
        if(win) {
            assertEquals(idChiara, controller.getCurrentGame().getMapWorld().getContinents().get("southAmerica").getIdOwner());
            assertEquals(idChiara, controller.getCurrentGame().getMapWorld().getContinents().get("australia").getIdOwner());
        }

        assertTrue(controller.updateTurnPhase(1));//to moveAtEnd phase
        assertTrue(controller.updateTurnPhase(1));//to fortify phase of Ale

        troopsToAddAtTheBeginning=controller.getActionController().calculateBasicTroopsAtFortify(2);
        NationsName n=controller.getCurrentGame().getMapWorld().getNations().stream().filter(x->x.getIdOwner()==2).findFirst().orElse(null).getNationName();
        controller.addTroopsToFortify( n, troopsToAddAtTheBeginning, 2);
        assertEquals(0 , controller.getActionController().getTroopsYetToBeDeployed());

        //using 3 cards after the beginning
        Player ale=controller.getCurrentGame().getPlayers().get(2);
        ale.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        ale.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        ale.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        assertTrue(controller.addTroopsUsingTerritoryCards(0,1,2,2));
        assertEquals(4,controller.getActionController().getTroopsYetToBeDeployed() );



        assertFalse(controller.updateTurnPhase(2));
        controller.addTroopsToFortify( n, 4 , 2);


        ale.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        ale.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        ale.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        ale.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        ale.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        ale.addTerritoryCards(new TerritoryCard("Cavalry",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
         controller.addTroopsUsingTerritoryCards(0,1,2,2);
        assertEquals(4, controller.getActionController().getTroopsYetToBeDeployed());
         controller.addTroopsUsingTerritoryCards(0,1,2,2);
        assertEquals(10, controller.getActionController().getTroopsYetToBeDeployed());

        assertFalse(controller.updateTurnPhase(2));
        controller.addTroopsToFortify( n, 10, 2);
        assertTrue(controller.updateTurnPhase(2));//to attack
        assertTrue(controller.updateTurnPhase(2));//to move
        assertTrue(controller.updateTurnPhase(2));//to fort

        Player alessia=controller.getCurrentGame().getPlayers().get(3);
        alessia.addTerritoryCards(new TerritoryCard("Infantry",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        alessia.addTerritoryCards(new TerritoryCard("cav",1, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        alessia.addTerritoryCards(new TerritoryCard("art",2, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        controller.addTroopsUsingTerritoryCards(0,1,2,3);
        assertEquals(10+ 3,controller.getActionController().getTroopsYetToBeDeployed() );

        //not working because the n isn't a nation of player 3 so i cannot put there my troops
        assertFalse(controller.addTroopsToFortify( n, 10, 3));

        controller.getCurrentGame().getMapWorld().getNation(NationsName.Ukraine).setIdOwner(3);
        n=(NationsName.Ukraine);
        assertTrue(controller.addTroopsToFortify( n, 10, 3));
        //isn't working beacause still 3 troops left to be deployed
        assertFalse(controller.updateTurnPhase(3));
        controller.addTroopsToFortify( n, 3, 3);
        assertTrue(controller.updateTurnPhase(3));//to attack

        assertTrue(controller.updateTurnPhase(3));//to move
        assertTrue(controller.updateTurnPhase(3));//to io fortify(reloop again)

        if(!win) {
            ArrayList<Nation> remNat = (ArrayList<Nation>) controller.getCurrentGame().getMapWorld().getNations().stream().filter(t -> t.getIdOwner() == 0).toList();
            assertTrue(controller.getCurrentGame().getMapWorld().getNations().stream().anyMatch(t -> t.getIdOwner() == 0));

        }
        assertEquals(PhaseTurn.fortify, controller.getPhase(0));
        assertEquals(PhaseTurn.wait, controller.getPhase(1));
        assertEquals(PhaseTurn.wait, controller.getPhase(2));
        assertEquals(PhaseTurn.wait, controller.getPhase(3));

        //now let's test the die of a player and the exchange of cards in that case

        if(! controller.addTroopsToFortify(NationsName.Venezuela, controller.getActionController().getTroopsYetToBeDeployed(),0 ))
        {
            System.out.println(" Problema non importante (in testGames)");
        }
        controller.addTroopsToFortify(NationsName.Venezuela, controller.getActionController().getTroopsYetToBeDeployed(),0 );
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela).removeAllTroops();
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela).addTroops(1);

        assertTrue(controller.updateTurnPhase(0));//go to attack
        assertTrue(controller.updateTurnPhase(0));//go to move
        assertTrue(controller.updateTurnPhase(0));//go to fort
        assertTrue(controller.addTroopsToFortify(NationsName.Peru, controller.getActionController().getTroopsYetToBeDeployed(),1 ));
        assertTrue(controller.updateTurnPhase(1));//go to attack

        controller.getCurrentGame().getMapWorld().getNation(NationsName.Peru).addTroops(40);
        controller.getCurrentGame().getPlayers().get(idIo).addTerritoryCards(new TerritoryCard("Infantr",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        controller.getCurrentGame().getPlayers().get(idIo).addTerritoryCards(new TerritoryCard("Infantr",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        controller.getCurrentGame().getPlayers().get(idIo).addTerritoryCards(new TerritoryCard("Infantr",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        controller.getCurrentGame().getPlayers().get(idIo).addTerritoryCards(new TerritoryCard("Infantr",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));


        controller.getCurrentGame().getPlayers().get(idChiara).addTerritoryCards(new TerritoryCard("Infantr",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        controller.getCurrentGame().getPlayers().get(idChiara).addTerritoryCards(new TerritoryCard("Infantr",0, new Nation(NationsName.Mongolia),NationsName.Mongolia));
        exit=false;
        while(!exit)
        {
            exit=controller.attack(controller.getCurrentGame().getMapWorld().getNation(NationsName.Peru) ,
                controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela),3,1);
        }


        ArrayList<Nation> remNat = (ArrayList<Nation>) controller.getCurrentGame().getMapWorld().getNations().stream().filter(t -> t.getIdOwner() == 0).toList();
        assertTrue(controller.getCurrentGame().getMapWorld().getNations().stream().noneMatch(t -> t.getIdOwner() == 0));
        assertFalse(controller.getCurrentGame().getPlayers().get(0).isAlive());
        assertEquals(7, controller.getCurrentGame().getPlayers().get(idChiara).getTerritoryCardsOwned().size());
        assertEquals(0, controller.getCurrentGame().getPlayers().get(idIo).getTerritoryCardsOwned().size());
        assertEquals(PhaseTurn.fortify, controller.getPhase(idChiara));
        assertFalse(controller.updateTurnPhase(idChiara));
        assertTrue(controller.addTroopsUsingTerritoryCards(0,1,4,1));
        controller.addTroopsToFortify(NationsName.Venezuela,4,1);
        assertTrue(controller.updateTurnPhase(idChiara));

        controller.getCurrentGame().getMapWorld().getNations().stream().forEach(t->t.setIdOwner(1));
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Peru).addTroops(20);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela).removeAllTroops();
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela).addTroops(1);

        exit=false;
        while(! exit) {
            exit = controller.attack(controller.getCurrentGame().getMapWorld().getNation(NationsName.Peru),
                    controller.getCurrentGame().getMapWorld().getNation(NationsName.Venezuela), 3, 1);
        }
        if(exit)
        {
            assertTrue(controller.isWinner());
        }

        assertFalse(controller.updateTurnPhase(1));
    }


}
