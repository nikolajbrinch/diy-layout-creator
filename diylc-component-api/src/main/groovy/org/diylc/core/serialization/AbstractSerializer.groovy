package org.diylc.core.serialization

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.ser.std.StdSerializer;


@CompileStatic
abstract class AbstractSerializer<T> extends StdSerializer<T> {

    public AbstractSerializer(Class<T> type) {
        super(type)
    }
    
}
