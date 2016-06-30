

package org.diylc.core.serialization

import java.awt.Color;
import java.awt.Font;

import groovy.transform.CompileStatic

import org.diylc.core.measures.Current
import org.diylc.core.measures.CurrentUnit

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

@CompileStatic
class FontDeserializer extends AbstractDeserializer<Font> {

    public FontDeserializer() {
        super(Font.class)
    }
    
    @Override
    public Font deserialize(JsonParser jsonParser, DeserializationContext ctxt) {
        ObjectMapper mapper = jsonParser.getCodec() as ObjectMapper
        
        TextNode node = mapper.readTree(jsonParser) as TextNode
       
        String fontName = node.asText()

        return new Font(fontName, 0, 12)
    }
    
}
