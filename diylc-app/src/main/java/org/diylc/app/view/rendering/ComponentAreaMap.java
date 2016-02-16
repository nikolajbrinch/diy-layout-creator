package org.diylc.app.view.rendering;

import java.awt.geom.Area;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.diylc.core.IDIYComponent;

public class ComponentAreaMap {

    private final Map<IDIYComponent, Area> map;

    private ComponentAreaMap(Map<IDIYComponent, Area> map) {
        this.map = map;
    }

    public ComponentAreaMap() {
        this.map = new HashMap<IDIYComponent, Area>();
    }

    public void put(IDIYComponent component, Area area) {
        map.put(component, area);
    }

    public void remove(IDIYComponent component) {
        map.remove(component);
    }

    public Area get(IDIYComponent component) {
        return map.get(component);
    }

    public void clear() {
        map.clear();
    }

    public static ComponentAreaMap emptyMap() {
        return new ComponentAreaMap(Collections.emptyMap());
    }

    public Collection<Area> values() {
        return map.values();
    }
    
}
