import Model.CurrentGame;
import Model.NationsName;
import Model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestBasicGame {

    @Test
    public void testBasicStart()
    {
        CurrentGame game = new CurrentGame();
        Player player1 =new Player("Beppe");
        Player player2 =new Player("Giacomo");
        Player player3 =new Player("Ellie");
        Player player4 =new Player("Marco");

        //player ready (in  cli e controller)

        //move to setup all the game (so in the controller, is needed a function for the division of :
            //objective cards ,
            //troops to each territory (divide all the card to all the players,
                                        // TODO
                                            //  for each player
                                            //define the number of max number of troops to place (to store in controller with map and using the number of player funct)
                                            //for each territory (0...n-1)
                                                //occupy with a troop
                                                //random number within the remaining troops
                                            //last territory will have all the remaning troops
            //


        //start the game

        //for each player (è del server)


            //fortify ->calculate the bonus
            //          put troops function (if the regiorn is admitted,        if we are in right phase)
            //          redeem troops                                           if we are in right phase)
            //          (implicit set flag attack=0, if the player attack in this turn set it to 1 then)

            //           if( deploy all the troops &&  have to have max 4 cards( if you have 4 you have to chose))
        //                          -> so  (change to next phase ==false) se non deployate
            // do that till player decide to go to next phase
            //attack ->
                        //TODO
            //          computeAttack(Nation start, Nation end,troopAttack,troopDef) -> true se ho bisogni di spostare chiedendo al client, false altrimenti
            //          (questa funzione starà nel action controller)
            //              resultsAttack (array[troopAttack])
            //              resultsDefend (array[troopDefend])
            //              for each troop attack
            //                   roll dice;   store the resultAttack
            //              for each troop defend
            //                   roll dice;   store the resultDefend
            //              ordinare entrambi risultati
            //              attackTroopLost=differenze tra i vari res
            //              defendTroopLost=total troops-attackTroopLost
            //              remove troops from attacker
            //              remove troops from defender
            //              if(defender Troops<0)
            //                  update territory owning
            //                  flag attack=1
            //                  if(troop in the attack territory >2)
            //                       return true
            //                  else
            //                       move 1 troop from attack to defender
            //                       return false
            //               return false

            //TODO

            //          hasWin()
            //            if(objective.effect is true)
            //            set win = true                        (win is a variable of the controller)
            //            change phase to ending
            //            the server will send to all players the message of the ending


            //           TODO moveTroops(Nation start, Nation to,troops)

            //               if(valid Movement in the controller && start and stop are of the owner)
            //                   remove x troop from the start nation and add to end

            // movement phase ->  if the player decide to move call the upper moveTroops function



    }
}
