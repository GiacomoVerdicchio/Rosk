package Server.Answer.Action;


import Server.Answer.Answer;

public abstract class ActionAnswer implements Answer
{
    public ActionAnswerENUM type;

    public ActionAnswerENUM getType() {
        return type;
    }
}
