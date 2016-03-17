package org.diylc.core.components.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.diylc.core.components.ComponentModel;
import org.diylc.core.components.properties.PropertyModel;

public class ComponentRegistry {

    private final Map<String, String> componentIds = new HashMap<>();
    
    private final Map<String, ComponentModel> components = new HashMap<>();

    private final ComponentModels componentModels;

    private final SpecificationRegistry specificationRegistry;

    ComponentRegistry(ComponentModels componentModels, SpecificationRegistry specificationRegistry) {
        this.componentModels = componentModels;
        this.specificationRegistry = specificationRegistry;
        
        for (ComponentModel componentModel : componentModels.getComponents()) {
            components.put(componentModel.getComponentId(), componentModel);
            componentIds.put(componentModel.getComponentClass().getName(), componentModel.getComponentId());
        }
    }
    
    Map<String, ComponentModel> getComponents() {
        return components;
    }

    public ComponentModels getComponentModels() {
        return componentModels;
    }

    public ComponentModel getComponentModel(String id) {
        return components.get(id);
    }

    public String getComponentModelId(String className) {
        return componentIds.get(className);
    }

    public List<PropertyModel> getSpecifications(String category) {
        return specificationRegistry.getSpecifications(category);
    }

}
