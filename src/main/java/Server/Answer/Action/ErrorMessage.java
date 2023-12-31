package Server.Answer.Action;

public class ErrorMessage extends ActionAnswer {
    private ErrorTypesENUM error;

    public ErrorMessage(ErrorTypesENUM error) {
        this.error = error;
        super.type = ActionAnswerENUM.ERROR_MESSAGE;

    }

    public ErrorTypesENUM getError() {
        return error;
    }
}
