package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.measures.AbstractMeasure
import org.diylc.core.serialization.AbstractDeserializer;

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.TextNode


@CompileStatic
abstract class AbstractMeasureDeserializer<T extends AbstractMeasure> extends AbstractDeserializer<T> {

    public static class Measure {
        double value
        
        String unit
    }
    
    public AbstractMeasureDeserializer(Class<T> type) {
        super(type)
    }
    
    protected Measure parseMeasure(JsonParser jsonParser) {
        ObjectMapper mapper = jsonParser.getCodec() as ObjectMapper
        
        TextNode node = mapper.readTree(jsonParser) as TextNode
       
        String textValue = node.asText()
        
        String valueString = textValue.substring(0, textValue.lastIndexOf(' '))
        
        double value = Double.valueOf(valueString.trim())
        
        String unit = textValue.substring(valueString.length()).trim()

        return new Measure('value' : value, 'unit' : unit)
    }
}
