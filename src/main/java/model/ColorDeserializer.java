package model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * A custom deserializer for {@link Color} objects from JSON data.
 * This deserializer is tailored to convert JSON objects that define the color components into {@link Color} instances.
 * The expected JSON format should include red, green, blue, and opacity values:
 * {
 *   "red": 1.0,
 *   "green": 1.0,
 *   "blue": 1.0,
 *   "opacity": 1.0
 * }
 */
public class ColorDeserializer extends StdDeserializer<Color> {

    /**
     * Constructs a {@code ColorDeserializer}.
     */
    public ColorDeserializer() {
        super(Color.class);
    }

    /**
     * Deserializes a JSON object into a {@link Color}.
     * Extracts the red, green, blue, and opacity components from the JSON object
     * and uses them to construct a new {@link Color} object.
     *
     * @param parser the JSON parser
     * @param context the deserialization context
     * @return the deserialized {@link Color} object
     * @throws IOException if an input/output error occurs during parsing
     */
    @Override
    public Color deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        return new Color(
                node.get("red").asDouble(),
                node.get("green").asDouble(),
                node.get("blue").asDouble(),
                node.get("opacity").asDouble()
        );
    }
}