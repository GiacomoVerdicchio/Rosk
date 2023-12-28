package Server.Answer.Setup;

import Server.Match;

import java.util.ArrayList;

public class ListOfLobbies extends SetupAnswer{
    private int maxMatchId;
    private ArrayList<String> list;

    public ListOfLobbies(ArrayList <String> m, int n) {
        this.list = m;
        this.maxMatchId =n;
        super.type = SetupAnswerENUM.LIST_LOBBIES;
    }

    public ArrayList<String> getList() {
        return list;
    }
    public int getMaxMatchId() {
        return maxMatchId;
    }
}
