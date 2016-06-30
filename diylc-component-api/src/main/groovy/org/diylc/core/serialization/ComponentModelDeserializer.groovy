



package org.diylc.core.serialization

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

import org.diylc.core.IDIYComponent
import org.diylc.core.components.ComponentModel
import org.diylc.core.components.registry.ComponentRegistry

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode;

@CompileStatic
class ComponentModelDeserializer extends AbstractDeserializer<ComponentModel> {

    private ComponentRegistry componentRegistry

    public ComponentModelDeserializer(ComponentRegistry componentRegistry) {
        super(ComponentModel.class)
        this.componentRegistry =  componentRegistry
    }

    @Override
    public ComponentModel deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = jsonParser.getCodec() as ObjectMapper
        
        TextNode node = mapper.readTree(jsonParser) as TextNode
       
        String textValue = node.asText()

        return componentRegistry.getComponentModel(textValue)
    }

}
