package Controller;

public class MineException extends Exception{
    String message;
    public MineException(String errorToShow) {
        this.message = errorToShow;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
