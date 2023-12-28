package Client.Messages.SetupMessages;

public class CreateOrJoinMessage extends SetupMessage{

    private boolean create;
    private int num;

    /**
     * Class Constructor, this message is used to tell the server our nickname after the connection has been established
     */
    public CreateOrJoinMessage(Boolean b, int num) {
        this.create = b;
        this.num = num;
        super.type = SetupMessageENUM.CREATE_OR_JOIN;
    }
    public CreateOrJoinMessage(Boolean b) {
        this.create = b;
        super.type = SetupMessageENUM.CREATE_OR_JOIN;
    }

    public boolean isCreateTrue() {
        return create;
    }

    public int getNum() {
        return num;
    }
}
