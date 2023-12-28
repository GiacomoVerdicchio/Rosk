package Client.Messages.SetupMessages;

/**
 * Message containing the player's nickname
 */
public class SetupName extends SetupMessage
{
    private String nickname;

    /**
     * Class Constructor, this message is used to tell the server our nickname after the connection has been established
     */
    public SetupName(String nickname) {
        this.nickname = nickname;
        super.type = SetupMessageENUM.SETUP_NAME;
    }

    public String getNickname() {
        return nickname;
    }
}
