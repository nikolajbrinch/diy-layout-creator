



package org.diylc.core.serialization

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

import org.diylc.core.IDIYComponent
import org.diylc.core.components.ComponentModel
import org.diylc.core.components.registry.ComponentFactory
import org.diylc.core.components.registry.ComponentRegistry

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode

@CompileStatic
class ComponentDeserializer extends AbstractDeserializer<IDIYComponent> {

    private ComponentRegistry componentRegistry

    private ComponentFactory componentFactory

    public ComponentDeserializer(ComponentRegistry componentRegistry, ComponentFactory componentFactory) {
        super(IDIYComponent.class)
        this.componentRegistry =  componentRegistry
        this.componentFactory =  componentFactory
    }

    @Override
    public IDIYComponent deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = jsonParser.getCodec() as ObjectMapper

        ObjectNode root = mapper.readTree(jsonParser) as ObjectNode

        JsonNode componentModelValue = root.findValue('componentModel')

        if (componentModelValue == null) {
            throw new IllegalStateException("Unable to find property 'componentModel' in node")
        }

        String componentModelId = componentModelValue.textValue()

        if (!componentModelId) {
            throw new IllegalStateException("Value is null or empty for property 'componentModel' in node")
        }

        ComponentModel componentModel = componentRegistry.getComponentModel(componentModelId)

        if (componentModel == null) {
            throw new IllegalStateException("Unable to find componentModel for ${handledType()} based on value '${componentModelId}' in 'componentModel'")
        }

        IDIYComponent component = componentFactory.createComponent(mapper, root, componentModel)

        return component
    }
}
