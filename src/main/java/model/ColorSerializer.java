package model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * A custom serializer for {@link Color} objects to JSON.
 * This serializer is designed to convert {@link Color} instances into a JSON object representing the color components (red, green, blue, opacity).
 * The output JSON format is:
 * {
 *   "red": 1.0,   // red component value
 *   "green": 1.0, // green component value
 *   "blue": 1.0,  // blue component value
 *   "opacity": 1.0  // opacity value
 * }
 */
public class ColorSerializer extends StdSerializer<Color> {

    /**
     * Constructs a {@code ColorSerializer}.
     */
    public ColorSerializer() {
        super(Color.class);
    }

    /**
     * Serializes a {@link Color} object into JSON.
     * Extracts the red, green, blue, and opacity values from the {@link Color} object
     * and writes them as named fields in a JSON object.
     *
     * @param value the {@link Color} object to serialize
     * @param generator the JSON generator used to write property names and values
     * @param provider the serializer provider that can be used to get serializers for serializing
     *                 the values of non-trivial types (non-primitive or standard types)
     * @throws IOException if a write-related error occurs during serialization
     */
    @Override
    public void serialize(Color value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        generator.writeNumberField("red", value.getRed());
        generator.writeNumberField("green", value.getGreen());
        generator.writeNumberField("blue", value.getBlue());
        generator.writeNumberField("opacity", value.getOpacity());
        generator.writeEndObject();
    }
}