package org.diylc.core.serialization

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

class FontSerializer extends AbstractSerializer<Font> {

    public FontSerializer() {
        super(Font.class)
    }
    
    @Override
    public void serialize(Font font, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(font.getFamily())
//        generator.writeStartObject()
//        generator.writeStringField('font-family', font.getFamily())
//        generator.writeNumberField('font-size', font.getSize2D())
//        generator.writeNumberField('font-style', font.getStyle())
//        generator.writeEndObject()
    }

}
