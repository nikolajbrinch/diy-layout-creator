

package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.measures.Current
import org.diylc.core.measures.CurrentUnit

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext

@CompileStatic
class CurrentDeserializer extends AbstractMeasureDeserializer<Current> {

    public CurrentDeserializer() {
        super(Current.class)
    }
    
    @Override
    public Current deserialize(JsonParser jsonParser, DeserializationContext ctxt) {
        AbstractMeasureDeserializer.Measure measure = parseMeasure(jsonParser)
        
        return new Current(measure.value, CurrentUnit.fromString(measure.unit))
    }
    
}
