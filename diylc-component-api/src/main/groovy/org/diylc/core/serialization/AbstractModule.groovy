package org.diylc.core.serialization

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule

import groovy.transform.CompileStatic;


@CompileStatic
abstract class AbstractModule<T> extends SimpleModule {

    public AbstractModule(String moduleName, Class<T> type, JsonDeserializer<? extends T> deserializer, JsonSerializer<T> serializer) {
        super(moduleName, new Version(1, 0, 0, null))
        addDeserializer(type, deserializer)
        addSerializer(type, serializer)
    }
}
