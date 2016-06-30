
package org.diylc.core.serialization

import groovy.transform.CompileStatic;

import org.diylc.core.measures.Capacitance;

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider

@CompileStatic
class CapacitanceSerializer extends AbstractMeasureSerializer<Capacitance> {

    public CapacitanceSerializer() {
        super(Capacitance.class, 'capacitance')
    }
    
}
