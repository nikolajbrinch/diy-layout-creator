package org.diylc.core.components.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.diylc.core.components.ComponentModel;
import org.diylc.specifications.Specification;
import org.diylc.specifications.registry.SpecificationRegistry;

public class DefaultComponentRegistry implements ComponentRegistry {

    private final Map<String, ComponentModel> componentModelsById = new HashMap<String, ComponentModel>();
    
    private final ComponentModels componentModels;

    private final SpecificationRegistry specificationRegistry;

    DefaultComponentRegistry(ComponentModels componentModels, SpecificationRegistry specificationRegistry) {
        this.componentModels = componentModels;
        this.specificationRegistry = specificationRegistry;
        
        for (ComponentModel componentModel : componentModels.getComponents()) {
            componentModelsById.put(componentModel.getId(), componentModel);
        }
    }
    
    @Override
    public ComponentModels getComponentModels() {
        return componentModels;
    }

    @Override
    public ComponentModel getComponentModel(String id) {
        return componentModelsById.get(id);
    }

    @Override
    public Collection<Specification> getSpecifications(String category) {
        return specificationRegistry.get(category);
    }

}
