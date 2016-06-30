package org.diylc.core.serialization

import java.io.IOException;

import org.diylc.core.measures.AbstractMeasure;
import org.diylc.core.measures.Capacitance;

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.ser.std.StdSerializer;


@CompileStatic
abstract class AbstractMeasureSerializer<T extends AbstractMeasure> extends AbstractSerializer<T> {

    final String name
    
    public AbstractMeasureSerializer(Class<T> type, String name) {
        super(type)
        this.name = name
    }
    
    @Override
    public void serialize(T measure, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString("${measure.value} ${measure.unit}")
//        generator.writeStartObject()
//        generator.writeStringField(name, "${measure.value} ${measure.unit}")
//        generator.writeEndObject()
    }

    
}
