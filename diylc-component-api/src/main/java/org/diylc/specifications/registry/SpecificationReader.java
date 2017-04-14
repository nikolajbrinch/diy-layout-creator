package org.diylc.specifications.registry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.diylc.specifications.Specification;
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
    
    public Specification read(String filename) {
        return read(new File(filename));
    }

    public Specification read(File file) {
        try (InputStream inputStream  = new FileInputStream(file)) {
            return read(inputStream);
        } catch (IOException e) {
            LOG.error("cannot read file " + file);
            throw new IllegalStateException(e);
        }
    }

    public Specification read(InputStream inputStream) throws JsonProcessingException, IOException {
        return parse(objectMapper.readTree(inputStream));
    }

    public Specification readFromString(String input) throws JsonProcessingException, IOException {
        return parse(objectMapper.readTree(input));
    }

    private Specification parse(JsonNode jsonNode) throws JsonProcessingException {
        Specification specification = null;
        
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
            Class<? extends Specification> specificationType = specificationTypeRegistry.lookup(category);
            LOG.trace("Creating specification [category = " + category + ", name = " + name + "] as " + specificationType);
            specification = objectMapper.treeToValue(jsonNode, specificationType);
        }
        
        return specification;
    }
}
