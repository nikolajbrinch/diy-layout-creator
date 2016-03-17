package org.diylc.core.components;


import org.diylc.core.components.properties.PropertyModel;

public class CustomPropertyModel implements PropertyModel {

    private String id;
    
    private String category;

    private String name;

    private String description;
    
    public CustomPropertyModel() {
        this.name = "Custom";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}
