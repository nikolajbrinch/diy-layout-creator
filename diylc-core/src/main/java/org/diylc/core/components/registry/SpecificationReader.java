package org.diylc.core.components.registry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.diylc.core.components.properties.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Reads a specification from an input source.
 * Specifications are JSON files, describing
 * 
 * @author neko
 *
 */
class SpecificationReader {

    private static final Logger LOG = LoggerFactory.getLogger(SpecificationReader.class);
    
    SpecificationTypeRegistry specificationTypeRegistry;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    public SpecificationReader(SpecificationTypeRegistry specificationTypeRegistry) {
        this.specificationTypeRegistry = specificationTypeRegistry;
    }
    
    public PropertyModel read(String filename) {
        return read(new File(filename));
    }

    public PropertyModel read(File file) {
        try (InputStream inputStream  = new FileInputStream(file)) {
            return read(inputStream);
        } catch (IOException e) {
            LOG.error("cannot read file " + file);
            throw new IllegalStateException(e);
        }
    }

    public PropertyModel read(InputStream inputStream) throws JsonProcessingException, IOException {
        return parse(objectMapper.readTree(inputStream));
    }

    public PropertyModel readFromString(String input) throws JsonProcessingException, IOException {
        return parse(objectMapper.readTree(input));
    }

    private PropertyModel parse(JsonNode jsonNode) throws JsonProcessingException {
        PropertyModel propertyModel = null;
        
        String category = null;
        String name = null;
        
        if (jsonNode.isContainerNode()) {
            JsonNode categoryNode = jsonNode.get("category");
            
            if (categoryNode.isValueNode() && categoryNode.isTextual()){
                category = categoryNode.textValue();
            }
            
            JsonNode nameNode = jsonNode.get("name");
            if (nameNode.isValueNode() && nameNode.isTextual()){
                name = nameNode.textValue();
            }
        }
        
        if (category != null) {
            Class<? extends PropertyModel> specificationType = specificationTypeRegistry.lookup(category);
            LOG.debug("Creating specification [category = " + category + ", name = " + name + "] as " + specificationType);
            propertyModel = objectMapper.treeToValue(jsonNode, specificationType);
        }
        
        return propertyModel;
    }
}
