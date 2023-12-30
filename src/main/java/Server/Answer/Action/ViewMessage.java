package Server.Answer.Action;

public class ViewMessage extends ActionAnswer {
    private String jsonView;

    public ViewMessage(String jsonView) {
        this.jsonView = jsonView;
    }

    public String getJsonView() {
        return jsonView;
    }
}
