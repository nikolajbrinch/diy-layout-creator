
package org.diylc.core.serialization

import groovy.transform.CompileStatic;

import org.diylc.core.measures.Capacitance;
import org.diylc.core.measures.Current;
import org.diylc.core.measures.Inductance;

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider

@CompileStatic
class CurrentSerializer extends AbstractMeasureSerializer<Current> {

    public CurrentSerializer() {
        super(Current.class, 'current')
    }
    
}
