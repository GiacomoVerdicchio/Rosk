package Controller;

public enum GamePhase {
    lobby, //collect all players and ready flags
    setup,  //setup territories, cards
    loop,   //true game
    ending  //win found
}
