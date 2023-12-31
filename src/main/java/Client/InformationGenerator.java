package Client;

import Client.LightView.LightView;
import Server.Answer.Action.ErrorTypesENUM;

public interface InformationGenerator {

    /**
     * Factory Method to produce the correct error message string based on the ERROR_TYPE received as input
     * @param error the type of error
     * @param view our current LightView
     * @return a string containing the error information
     */
    default String errorGenerator(ErrorTypesENUM error, LightView view)
    {
        switch(error)
        {
            case wrong_phase:
                return "Wrong Phase!";

            default:
                return "Generic Error";
        }
    }



}
