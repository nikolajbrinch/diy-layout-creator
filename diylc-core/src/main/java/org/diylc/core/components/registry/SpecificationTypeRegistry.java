package org.diylc.core.components.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.diylc.core.components.properties.PropertyModel;

public class SpecificationTypeRegistry {

    private Map<String, Class<? extends PropertyModel>> specificationTypes = new HashMap<String, Class<? extends PropertyModel>>();

    public Class<? extends PropertyModel> lookup(String category) {
        return specificationTypes.get(category);
    }

    public void add(String category, Class<? extends PropertyModel> specificationType) {
        specificationTypes.put(category, specificationType);
    }

    public Collection<Class<? extends PropertyModel>> getTypes() {
        return specificationTypes.values();
    }

    public Set<String> getCategories() {
        return specificationTypes.keySet();
    }
}
