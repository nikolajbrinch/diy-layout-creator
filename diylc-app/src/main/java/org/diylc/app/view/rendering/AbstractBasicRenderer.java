package org.diylc.app.view.rendering;

import java.awt.Point;
import java.awt.geom.Area;
import java.util.Collections;
import java.util.List;

import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;

public abstract class AbstractBasicRenderer implements Renderer {

    @Override
    public void invalidateComponent(IDIYComponent component) {
    }

    @Override
    public List<IDIYComponent> findComponentsAt(Point point, Project project) {
        return Collections.emptyList();
    }

    @Override
    public Area getComponentArea(IDIYComponent component) {
        return null;
    }

    @Override
    public void clearComponentAreaMap() {
    }

    @Override
    public ComponentAreaMap getComponentAreaMap() {
        return ComponentAreaMap.emptyMap();
    }

}
