package Server.Answer.Action;

public class ErrorMessage extends ActionAnswer {
    private final String message;

    public ErrorMessage(String message) {
        this.message = message;
        super.type = ActionAnswerENUM.ERROR_MESSAGE;

    }

    public String getMessage() {
        return message;
    }
}
