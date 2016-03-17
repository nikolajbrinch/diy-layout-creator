package org.diylc.core.components.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.diylc.core.components.properties.PropertyModel;

public class SpecificationRegistry {

    private Map<String, Map<String, PropertyModel>> specifications = new HashMap<String, Map<String, PropertyModel>>();
    
    public void add(PropertyModel propertyModel) {
        Map<String, PropertyModel> specifications = this.specifications.get(propertyModel.getCategory());

        if (specifications == null) {
            specifications = new HashMap<String, PropertyModel>();
            this.specifications.put(propertyModel.getCategory(), specifications);
        }

        specifications.put(propertyModel.getId(), propertyModel);
    }

    public List<PropertyModel> getSpecifications(String category) {
        List<PropertyModel> propertyModels = null;
        
        if (category != null) {
            propertyModels = new ArrayList<>(this.specifications.get(category).values());
        } 
        
        return propertyModels;
    }

}
