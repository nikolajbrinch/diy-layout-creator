package org.diylc.core;

import java.util.HashSet;
import java.util.Set;

public class Group {

    private Set<String> components = new HashSet<String>();
    
    public Group() {
    }
    
    public Set<String> getComponents() {
        return components;
    }

    public void addComponent(String id) {
        components.add(id);
    }
    
    public void removeComponent(String id) {
        components.remove(id);
    }

    public boolean containsComponent(String id) {
        return components.contains(id);
    }

}
