package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.measures.Inductance
import org.diylc.core.measures.InductanceUnit

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext

@CompileStatic
class InductanceDeserializer extends AbstractMeasureDeserializer<Inductance> {

    public InductanceDeserializer() {
        super(Inductance.class)
    }
    
    @Override
    public Inductance deserialize(JsonParser jsonParser, DeserializationContext ctxt) {
        AbstractMeasureDeserializer.Measure measure = parseMeasure(jsonParser)
        
        return new Inductance(measure.value, InductanceUnit.fromString(measure.unit))
    }
    
}
