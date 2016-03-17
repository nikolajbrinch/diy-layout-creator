package org.diylc.components.registry;

import java.util.Comparator;

import org.diylc.core.components.ComponentModel;
import org.diylc.core.components.IDIYComponent;
import org.diylc.core.components.properties.PropertyDescriptor;
import org.diylc.core.components.properties.PropertyModel;

public class ComparatorFactory {

    private static ComparatorFactory instance = new ComparatorFactory();

    private Comparator<IDIYComponent> componentNameComparator;
    
    private Comparator<ComponentModel> componentTypeComparator;
    
    private Comparator<PropertyDescriptor> propertyNameComparator;
    
    private Comparator<IDIYComponent> componentZOrderComparator;

    private Comparator<PropertyModel> specificationNameComparator;
    
    public static ComparatorFactory getInstance() {
        return instance;
    }

    public Comparator<IDIYComponent> getComponentNameComparator() {
        if (componentNameComparator == null) {
            componentNameComparator = new Comparator<IDIYComponent>() {

                @Override
                public int compare(IDIYComponent o1, IDIYComponent o2) {
                    String name1 = o1.getName();
                    String name2 = o2.getName();
                    if (name1 == null || name2 == null) {
                        return 0;
                    }
                    return name1.compareToIgnoreCase(name2);
                }
            };
        }
        return componentNameComparator;
    }

    public Comparator<ComponentModel> getComponentTypeComparator() {
        if (componentTypeComparator == null) {
            componentTypeComparator = new Comparator<ComponentModel>() {

                @Override
                public int compare(ComponentModel o1, ComponentModel o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            };
        }
        return componentTypeComparator;
    }

    public Comparator<PropertyDescriptor> getPropertyNameComparator() {
        if (propertyNameComparator == null) {
            propertyNameComparator = new Comparator<PropertyDescriptor>() {

                @Override
                public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }

            };
        }
        return propertyNameComparator;
    }

    public Comparator<IDIYComponent> getComponentZOrderComparator() {
        if (componentZOrderComparator == null) {
            componentZOrderComparator = new Comparator<IDIYComponent>() {

                @Override
                public int compare(IDIYComponent o1, IDIYComponent o2) {
                    ComponentModel model1 = o1.getComponentModel();
                    ComponentModel model2 = o2.getComponentModel();
                    
                    return new Double(model1.getZOrder()).compareTo(new Double(model2.getZOrder()));
                }
            };
        }
        return componentZOrderComparator;
    }

    public Comparator<PropertyModel> getSpecificationNameComparator() {
        if (specificationNameComparator == null) {
            specificationNameComparator = new Comparator<PropertyModel>() {

                @Override
                public int compare(PropertyModel o1, PropertyModel o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            };
        }
        return specificationNameComparator;
    }
}
