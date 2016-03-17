package org.diylc.core.components.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.diylc.core.components.ComponentModel;

public class ComponentModels {

    Map<String, List<ComponentModel>> componentTypesByCategory = new HashMap<String, List<ComponentModel>>();

    Set<String> categories = new HashSet<String>();

    List<ComponentModel> componentModels = new ArrayList<ComponentModel>();

    public void add(ComponentModel componentModel) {
        String category = componentModel.getCategory();
        List<ComponentModel> components = componentTypesByCategory.get(category);

        if (components == null) {
            components = new ArrayList<ComponentModel>();
            componentTypesByCategory.put(category, components);
        }

        components.add(componentModel);
        componentModels.add(componentModel);
        categories.add(category);
    }

    public List<ComponentModel> getComponents(String category) {
        return componentTypesByCategory.get(category);
    }

    public List<ComponentModel> getComponents() {
        return componentModels;
    }

    public List<String> getCategories() {
        return new ArrayList<String>(categories);
    }

}
