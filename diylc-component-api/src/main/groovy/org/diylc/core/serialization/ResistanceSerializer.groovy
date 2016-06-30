
package org.diylc.core.serialization

import groovy.transform.CompileStatic;

import org.diylc.core.measures.Resistance

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider

@CompileStatic
class ResistanceSerializer extends AbstractMeasureSerializer<Resistance> {

    public ResistanceSerializer() {
        super(Resistance.class, 'resistance')
    }
    
}
