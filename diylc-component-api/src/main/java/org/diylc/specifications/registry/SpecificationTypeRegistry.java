package org.diylc.specifications.registry;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.diylc.specifications.Specification;

public class SpecificationTypeRegistry {

    private Map<String, Class<? extends Specification>> specificationTypes = new ConcurrentHashMap<String, Class<? extends Specification>>();

    public Class<? extends Specification> lookup(String category) {
        return specificationTypes.get(category);
    }

    public void add(String category, Class<? extends Specification> specificationType) {
        specificationTypes.put(category, specificationType);
    }

    public Collection<Class<? extends Specification>> getTypes() {
        return specificationTypes.values();
    }

    public Set<String> getCategories() {
        return specificationTypes.keySet();
    }
}
