package Server.Answer.Action;

import static Server.Answer.Action.ActionAnswerENUM.VIEW;

public class ViewMessage extends ActionAnswer {
    private String jsonView;

    public ViewMessage(String jsonView) {
        this.jsonView = jsonView;
        super.type=VIEW;
    }

    public String getJsonView() {
        return jsonView;
    }
}
