package org.diylc.components;

import java.util.Comparator;

import org.diylc.core.IDIYComponent;
import org.diylc.core.PropertyWrapper;

public class ComparatorFactory {

    private static ComparatorFactory instance = new ComparatorFactory();

    private Comparator<IDIYComponent<?>> componentNameComparator;
    private Comparator<ComponentType> componentTypeComparator;
    private Comparator<PropertyWrapper> propertyNameComparator;
    private Comparator<IDIYComponent<?>> componentZOrderComparator;
    private ComponentTypeFactory componentTypeFactory = new ComponentTypeFactory();

    public static ComparatorFactory getInstance() {
        return instance;
    }

    public Comparator<IDIYComponent<?>> getComponentNameComparator() {
        if (componentNameComparator == null) {
            componentNameComparator = new Comparator<IDIYComponent<?>>() {

                @Override
                public int compare(IDIYComponent<?> o1, IDIYComponent<?> o2) {
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

    public Comparator<ComponentType> getComponentTypeComparator() {
        if (componentTypeComparator == null) {
            componentTypeComparator = new Comparator<ComponentType>() {

                @Override
                public int compare(ComponentType o1, ComponentType o2) {
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

    public Comparator<IDIYComponent<?>> getComponentZOrderComparator() {
        if (componentZOrderComparator == null) {
            componentZOrderComparator = new Comparator<IDIYComponent<?>>() {

                @Override
                public int compare(IDIYComponent<?> o1, IDIYComponent<?> o2) {
                    ComponentType type1 = componentTypeFactory.newComponentType(o1);
                    ComponentType type2 = componentTypeFactory.newComponentType(o2);

                    return new Double(type1.getZOrder()).compareTo(type2.getZOrder());
                }
            };
        }
        return componentZOrderComparator;
    }
}
