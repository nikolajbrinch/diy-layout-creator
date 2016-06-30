package org.diylc.core.serialization

import java.awt.Color;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

class ColorSerializer extends AbstractSerializer<Color> {

    public ColorSerializer() {
        super(Color.class)
    }
    
    @Override
    public void serialize(Color color, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString("#" + Integer.toHexString(color.getRGB()))
    }

}
