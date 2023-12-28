package Client;

import Client.LightView.LightView;
import Server.Answer.Action.ActionAnswer;
import Server.Answer.Setup.SetupAnswer;

public interface ClientInterface {

        //TODO
        LightView MyView = new LightView();

        void run();

        void readMessage();

        void setupHandler(SetupAnswer answer);

        void messageHandler(ActionAnswer answer);
}
