

package org.diylc.core.serialization

import java.awt.Color;

import groovy.transform.CompileStatic

import org.diylc.core.measures.Current
import org.diylc.core.measures.CurrentUnit

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

@CompileStatic
class ColorDeserializer extends AbstractDeserializer<Color> {

    public ColorDeserializer() {
        super(Color.class)
    }
    
    @Override
    public Color deserialize(JsonParser jsonParser, DeserializationContext ctxt) {
        ObjectMapper mapper = jsonParser.getCodec() as ObjectMapper
        
        TextNode node = mapper.readTree(jsonParser) as TextNode
       
        String textValue = node.asText()

        String hexString = textValue.substring(1)
        
        int color = Integer.parseUnsignedInt(hexString, 16)
        
        return new Color(color, true)
    }
    
}
