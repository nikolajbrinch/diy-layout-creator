package org.diylc.components.registry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.diylc.core.ComponentType;

public class ComponentTypes {

    Map<String, List<ComponentType>> categoryTypes = new ConcurrentHashMap<String, List<ComponentType>>();

    Set<String> categories = new HashSet<String>();

    List<ComponentType> types = new ArrayList<ComponentType>();

    public void add(ComponentType componentType) {
        String category = componentType.getCategory();

        List<ComponentType> components = categoryTypes.get(category);

        if (components == null) {
            components = new ArrayList<ComponentType>();
            categoryTypes.put(category, components);
        }

        components.add(componentType);
        types.add(componentType);
        categories.add(category);
    }

    public List<ComponentType> getComponents(String category) {
        return categoryTypes.get(category);
    }

    public List<ComponentType> getComponents() {
        return types;
    }

    public List<String> getCategories() {
        return new ArrayList<String>(categories);
    }

}
