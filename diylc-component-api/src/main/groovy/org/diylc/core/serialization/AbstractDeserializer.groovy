package org.diylc.core.serialization

import java.io.IOException;

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode


@CompileStatic
abstract class AbstractDeserializer<T> extends StdDeserializer<T> {

    public AbstractDeserializer(Class<T> type) {
        super(type)
    }
    
}
