package Client.Messages.SetupMessages;

public class ChosenLobby extends SetupMessage{
    private  int idLobby;
    public ChosenLobby(int numLobby) {
        this.idLobby=numLobby;
        super.type = SetupMessageENUM.CHOSEN_LOBBY;
    }

    public int getId()
    {
        return idLobby;
    }
}
