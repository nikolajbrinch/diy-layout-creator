package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.measures.Power
import org.diylc.core.measures.PowerUnit

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext

@CompileStatic
class PowerDeserializer extends AbstractMeasureDeserializer<Power> {

    public PowerDeserializer() {
        super(Power.class)
    }
    
    @Override
    public Power deserialize(JsonParser jsonParser, DeserializationContext ctxt) {
        AbstractMeasureDeserializer.Measure measure = parseMeasure(jsonParser)
        
        return new Power(measure.value, PowerUnit.fromString(measure.unit))
    }
    
}
