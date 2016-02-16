package org.diylc.app.view.rendering;

import java.awt.Point;
import java.awt.geom.Area;
import java.util.List;

import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;

public interface Renderer {

    public List<IDIYComponent> render(RenderingContext renderingContext);

    public void invalidateComponent(IDIYComponent component);

    public List<IDIYComponent> findComponentsAt(Point point, Project project);

    public Area getComponentArea(IDIYComponent component);

    public void clearComponentAreaMap();

    public ComponentAreaMap getComponentAreaMap();

}
