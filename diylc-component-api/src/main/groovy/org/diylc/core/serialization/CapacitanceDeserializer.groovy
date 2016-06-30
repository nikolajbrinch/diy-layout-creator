package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.measures.Capacitance
import org.diylc.core.measures.CapacitanceUnit

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext

@CompileStatic
class CapacitanceDeserializer extends AbstractMeasureDeserializer<Capacitance> {

    public CapacitanceDeserializer() {
        super(Capacitance.class)
    }
    
    @Override
    public Capacitance deserialize(JsonParser jsonParser, DeserializationContext ctxt) {
        AbstractMeasureDeserializer.Measure measure = parseMeasure(jsonParser)
        
        return new Capacitance(measure.value, CapacitanceUnit.fromString(measure.unit))
    }
    
}
