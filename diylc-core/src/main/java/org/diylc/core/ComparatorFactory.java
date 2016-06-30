package org.diylc.core;

import java.util.Comparator;

import org.diylc.core.components.ComponentModel;
import org.diylc.specifications.Specification;

public class ComparatorFactory {

    private static ComparatorFactory instance = new ComparatorFactory();

    private Comparator<IDIYComponent> componentNameComparator;
    
    private Comparator<ComponentModel> componentTypeComparator;
    
    private Comparator<PropertyWrapper> propertyNameComparator;
    
    private Comparator<IDIYComponent> componentZOrderComparator;

    private Comparator<Specification> specificationNameComparator;
    
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

    public Comparator<PropertyWrapper> getPropertyNameComparator() {
        if (propertyNameComparator == null) {
            propertyNameComparator = new Comparator<PropertyWrapper>() {

                @Override
                public int compare(PropertyWrapper o1, PropertyWrapper o2) {
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
                    ComponentModel type1 = o1.getComponentModel();
                    ComponentModel type2 = o2.getComponentModel();

                    return new Double(type1.getzOrder()).compareTo(new Double(type2.getzOrder()));
                }
            };
        }
        return componentZOrderComparator;
    }

    public Comparator<Specification> getSpecificationNameComparator() {
        if (specificationNameComparator == null) {
            specificationNameComparator = new Comparator<Specification>() {

                @Override
                public int compare(Specification o1, Specification o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            };
        }
        return specificationNameComparator;
    }
}
