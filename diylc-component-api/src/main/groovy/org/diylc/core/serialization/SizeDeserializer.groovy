package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext

@CompileStatic
class SizeDeserializer extends AbstractMeasureDeserializer<Size> {

    public SizeDeserializer() {
        super(Size.class)
    }
    
    @Override
    public Size deserialize(JsonParser jsonParser, DeserializationContext ctxt) {
        AbstractMeasureDeserializer.Measure measure = parseMeasure(jsonParser)
        
        return new Size(measure.value, measure.unit as SizeUnit)
    }
    
}
