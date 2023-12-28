package Server.Answer.Setup;

import Server.Answer.Answer;

/**
 * Abstract class from which the SetupAnswers inherit the parameter type and its getter
 */
public abstract class SetupAnswer implements Answer {
    public SetupAnswerENUM type;

    public SetupAnswerENUM getType() {
        return type;
    }
}
