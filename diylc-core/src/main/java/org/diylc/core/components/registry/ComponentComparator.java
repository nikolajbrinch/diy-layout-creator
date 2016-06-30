package org.diylc.core.components.registry;

import java.util.Comparator;

import org.diylc.core.IDIYComponent;

public class ComponentComparator implements Comparator<Class<? extends IDIYComponent>> {

    @Override
    public int compare(Class<? extends IDIYComponent> o1, Class<? extends IDIYComponent> o2) {
        return o1.getName().compareTo(o2.getName());
    }
    
}
