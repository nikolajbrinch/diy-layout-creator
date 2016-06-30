package org.diylc.core.serialization

import groovy.transform.CompileStatic;

import org.diylc.core.measures.Voltage

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider

@CompileStatic
class VoltageSerializer extends AbstractMeasureSerializer<Voltage> {

    public VoltageSerializer() {
        super(Voltage.class, 'voltage')
    }
    
}
