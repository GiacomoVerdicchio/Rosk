package Server.Answer.Setup;

import java.util.ArrayList;

public class UpdateReadyPlayers extends SetupAnswer{

    private ArrayList<String> list;

    public UpdateReadyPlayers(ArrayList <String> players) {
        this.list = players;
        super.type = SetupAnswerENUM.UPDATE_READY_PLAYERS;
    }

    public ArrayList<String> getList() {
        return list;
    }
}
