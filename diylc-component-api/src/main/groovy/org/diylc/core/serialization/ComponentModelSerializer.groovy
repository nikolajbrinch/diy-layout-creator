
package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.components.ComponentModel

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider

@CompileStatic
class ComponentModelSerializer extends AbstractSerializer<ComponentModel> {

    public ComponentModelSerializer() {
        super(ComponentModel.class)
    }
    
    @Override
    public void serialize(ComponentModel componentModel, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(componentModel.getId())
    }

}
