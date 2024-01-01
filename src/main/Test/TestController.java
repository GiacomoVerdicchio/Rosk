import Controller.Controller;
import Model.Nation;
import Model.NationsName;
import org.junit.jupiter.api.Test;
import Controller.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestController {
    private Controller controller;

    @Test
    public void testAddPeople() {
        controller = new Controller();
        assertFalse(controller.getSetupController().isReadyAllPlayers());


        //verify that it doesn't work with only 1 person
        controller.getSetupController().addPlayer("Beppe");
        assertFalse(controller.getSetupController().isReadyAllPlayers());
        assertEquals(1, controller.getCurrentGame().getPlayers().size());

        //verify that the remove works correctly
        controller.getSetupController().addPlayer("Io");
        controller.getSetupController().removePlayer("Io");
        assertEquals(1, controller.getCurrentGame().getPlayers().size());
    }


    @Test
    public void testReady() {
        controller = new Controller();

        try {
            //try adding ready or remove it to player when no players
            controller.getSetupController().addReady("Beppe");
            controller.getSetupController().removeReady("Beppe");
            //should print that no players in the game
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage()+ "testController");
        }

        try{
            //see if add and remove works
            controller.getSetupController().addPlayer("Io");
            controller.getSetupController().addReady("Io");
            assertEquals(1, controller.getCurrentGame().getPlayers().size());
            assertEquals(1,controller.getSetupController().getReady().size());
            assertEquals(true,controller.getSetupController().getReady().get("Io"));
            controller.getSetupController().removeReady("Io");
            assertEquals(1, controller.getCurrentGame().getPlayers().size());
            assertEquals(1,controller.getSetupController().getReady().size());
            assertEquals(false,controller.getSetupController().getReady().get("Io"));
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage()+ "testController");
        }

        //see if I can add a ready to a name that isn't in the game
        try{
            controller.getSetupController().addReady("Ellie");
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage()+ "testController");
        }
        try{
            controller.getSetupController().removeReady("Ellie");
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage()+ "testController");
        }

        System.out.println("Now should be right");
        try{
            assertEquals(false, controller.getSetupController().isReadyAllPlayers());
            controller.getSetupController().addPlayer("Ellie");
            controller.getSetupController().addPlayer("Marco");
            controller.getSetupController().addReady("Ellie");
            controller.getSetupController().addReady("Marco");
            controller.getSetupController().addReady("Io");
            controller.getSetupController().removeReady("Io");
            controller.getSetupController().addReady("Io");
            assertEquals(true, controller.getSetupController().isReadyAllPlayers());
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        }


    }


    @Test
    public void testFillTerritory()
    {
        controller = new Controller();
        controller.getSetupController().addPlayer("Beppe");
        controller.getSetupController().addPlayer("Io");
        controller.getSetupController().addPlayer("Ellie");
        controller.getSetupController().addPlayer("Marco");

        try {
            controller.getSetupController().addReady("Beppe");
            controller.getSetupController().addReady("Io");
            controller.getSetupController().addReady("Ellie");
            controller.getSetupController().addReady("Marco");




            controller.generalSetupAfterReady();
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage()+ "testController");
        }

        assertEquals(44, controller.getCurrentGame().getTerritoriesDeck().size());
        int partialSum;
        for(int i=0;i<controller.getCurrentGame().getPlayers().size();i++) {
            partialSum = 0;
            for (Nation n : controller.getCurrentGame().getMapWorld().getNations()) {
                if (n.getIdOwner() == i)
                    partialSum += n.getTroops();
            }
            assertEquals(30, partialSum);
        }

    }

    @Test
    public void testNationReachable()
    {
        testFillTerritory();
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.Kamchatka).setIdOwner(0);
        controller.getCurrentGame().getMapWorld().getNation(NationsName.NorthAfrica).setIdOwner(1);
        assertTrue(Checks.isNationReachable(controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan),controller.getCurrentGame().getMapWorld().getNation(NationsName.Mongolia), 0,controller.getCurrentGame().getMapWorld()));
        assertTrue(Checks.isNationReachable(controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan),controller.getCurrentGame().getMapWorld().getNation(NationsName.Kamchatka), 0,controller.getCurrentGame().getMapWorld()));
        assertFalse(Checks.isNationReachable(controller.getCurrentGame().getMapWorld().getNation(NationsName.Japan),controller.getCurrentGame().getMapWorld().getNation(NationsName.NorthAfrica), 0,controller.getCurrentGame().getMapWorld()));
    }

    @Test
    public void testUpdateGamePhase()
    {
        testFillTerritory(); //already update the gamephase
        assertNotEquals( GamePhase.setup, controller.getGamePhase());


        assertEquals(GamePhase.loop, controller.getGamePhase());
        controller.updateGamePhase();
        assertEquals(GamePhase.ending, controller.getGamePhase());
    }

}