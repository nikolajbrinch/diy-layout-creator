package org.diylc.core.components.registry;

import org.diylc.core.IDIYComponent;
import org.diylc.core.components.ComponentModel;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public interface ComponentFactory {
    
    public IDIYComponent createComponent(ObjectMapper mapper, ObjectNode root, ComponentModel componentModel);

    public IDIYComponent createComponent(ComponentModel componentModel) throws Exception;
    
    public IDIYComponent createComponent(IDIYComponent component);
    
}
