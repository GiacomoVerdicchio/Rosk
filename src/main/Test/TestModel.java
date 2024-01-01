import Model.CurrentGame;
import Model.NationsName;
import Model.TerritoryCard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestModel {

    @Test
    public void testBasic() {
        CurrentGame current = new CurrentGame();
        current.getMapWorld().getNations().stream().map(t->t.getNationName()).forEach(System.out::println);

        assertTrue(current.getMapWorld().getNation(NationsName.WestAustralia)!=null);
        assertEquals(current.getMapWorld().getNation(NationsName.WestAustralia).getNeighbors().size(),3);

        //test confini
        assertTrue(current.getMapWorld().getNation(NationsName.Japan).getNeighbors().contains(NationsName.Kamchatka));
        assertTrue(current.getMapWorld().getNation(NationsName.Japan).getNeighbors().contains(NationsName.Mongolia));

        //remove territory card from the remaining pile
        current.refillDeckTerritoriesRemaining();
        for(TerritoryCard t:  current.getTerritoriesRemainingDeck())
        {
            assertTrue(current.getTerritoriesRemainingDeck().contains(t));
        }

        //try to simulate the draw of the card
        TerritoryCard ter=null;
        for(int i=0; i<42;i++) {
            ter=current.drawCardFromRemaingPile();
        }
        assertEquals(0,current.getTerritoriesRemainingDeck().size());
        assertFalse(current.getTerritoriesRemainingDeck().contains(ter));
        //check the ripopulation of the remaining pile
        ter=current.drawCardFromRemaingPile();
        assertEquals(42-1,current.getTerritoriesRemainingDeck().size());
        System.out.println("Let's go (testModel)");
    }



    @Test
    public void testBasicSuSerial() {
        CurrentGame current = new CurrentGame();
        current.addPlayer("adlòs");

        System.out.println(current.modelToJson());
    }
}
