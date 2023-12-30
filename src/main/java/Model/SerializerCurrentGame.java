package Model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class SerializerCurrentGame extends StdSerializer<CurrentGame>{

    public SerializerCurrentGame()
    {
        this(null);
    }

    /**
     * Class constructor, uses the parent class constructor to bind itself to the current game state
     * @param currentGame an instance of the current game state
     */
    public SerializerCurrentGame(Class<CurrentGame> currentGame) {
        super(currentGame);
    }


    /**
     * Method serialize creates and fills the json file with serialized model objects
     * @param currentGame the current game
     * @param generator the json Generator
     * @param p the serializer provider
     * @throws IOException if it fails  to serialize
     */
    @Override
    public void serialize(CurrentGame currentGame, JsonGenerator generator, SerializerProvider p) throws IOException {
        generator.writeStartObject();

        generator.writeObjectField("mapWorld", currentGame.getMapWorld());
        generator.writeObjectField("players", currentGame.getPlayers());
        generator.writeObjectField("territoriesDeck", currentGame.getTerritoriesDeck());

        generator.writeEndObject();
    }
}
