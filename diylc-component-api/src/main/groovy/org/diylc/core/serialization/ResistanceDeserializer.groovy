package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.measures.Resistance
import org.diylc.core.measures.ResistanceUnit

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext

@CompileStatic
class ResistanceDeserializer extends AbstractMeasureDeserializer<Resistance> {

    public ResistanceDeserializer() {
        super(Resistance.class)
    }
    
    @Override
    public Resistance deserialize(JsonParser jsonParser, DeserializationContext ctxt) {
        AbstractMeasureDeserializer.Measure measure = parseMeasure(jsonParser)
        
        return new Resistance(measure.value, ResistanceUnit.fromString(measure.unit))
    }
    
}
