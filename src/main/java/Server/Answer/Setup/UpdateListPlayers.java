package Server.Answer.Setup;

import Server.Match;

import java.util.ArrayList;

public class UpdateListPlayers extends SetupAnswer{
    private ArrayList<String> list;

    public UpdateListPlayers(ArrayList <String> players) {
        this.list = players;
        super.type = SetupAnswerENUM.UPDATE_LIST_PLAYERS;
    }

    public ArrayList<String> getList() {
        return list;
    }
}
