package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.measures.Voltage
import org.diylc.core.measures.VoltageUnit

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext

@CompileStatic
class VoltageDeserializer extends AbstractMeasureDeserializer<Voltage> {

    public VoltageDeserializer() {
        super(Voltage.class)
    }
    
    @Override
    public Voltage deserialize(JsonParser jsonParser, DeserializationContext ctxt) {
        AbstractMeasureDeserializer.Measure measure = parseMeasure(jsonParser)
        
        return new Voltage(measure.value, VoltageUnit.fromString(measure.unit))
    }
    
}
