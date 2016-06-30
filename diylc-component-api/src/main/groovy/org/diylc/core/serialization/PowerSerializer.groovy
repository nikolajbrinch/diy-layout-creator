
package org.diylc.core.serialization

import groovy.transform.CompileStatic;

import org.diylc.core.measures.Power;

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider

@CompileStatic
class PowerSerializer extends AbstractMeasureSerializer<Power> {

    public PowerSerializer() {
        super(Power.class, 'power')
    }
    
}
