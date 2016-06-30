package org.diylc.app.view.rendering;

import java.util.HashMap;
import java.util.Map;

import org.diylc.core.IDIYComponent;
import org.diylc.core.components.ComponentState;

public class ComponentStateMap {

    private Map<IDIYComponent, ComponentState> map = new HashMap<IDIYComponent, ComponentState>();

    public ComponentState get(IDIYComponent component) {
        return map.get(component);
    }

    public void put(IDIYComponent component, ComponentState state) {
        map.put(component, state);
    }

    public void remove(IDIYComponent component) {
        map.remove(component);
    }

    public void clear() {
        map.clear();
    }

}
