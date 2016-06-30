package org.diylc.core.serialization

import groovy.transform.CompileStatic;

import org.diylc.core.measures.Size

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider

@CompileStatic
class SizeSerializer extends AbstractMeasureSerializer<Size> {

    public SizeSerializer() {
        super(Size.class, 'size')
    }
    
}
