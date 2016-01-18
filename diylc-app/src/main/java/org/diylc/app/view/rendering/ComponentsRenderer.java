package org.diylc.app.view.rendering;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.diylc.components.IComponentFilter;
import org.diylc.core.ComponentState;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.graphics.GraphicsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentsRenderer implements Renderer {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentsRenderer.class);

    private static final Composite lockedComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);

    /*
     * Keeps Area object of each drawn component.
     */
    private Map<IDIYComponent<?>, Area> componentAreaMap = new HashMap<IDIYComponent<?>, Area>();

    /*
     * Maps components to the last state they are drawn in. Also, used to
     * determine which components are invalidated when they are not in the map.
     */
    private Map<IDIYComponent<?>, ComponentState> lastDrawnStateMap = new HashMap<IDIYComponent<?>, ComponentState>();

    @Override
    public List<IDIYComponent<?>> render(RenderingContext renderingContext) {
        Graphics2D graphics2d = renderingContext.getGraphics2d();
        Graphics2DWrapper graphics2dWrapper = renderingContext.getGraphics2dWrapper();
        Project project = renderingContext.getProject();
        List<IDIYComponent<?>> components = project.getComponents();
        List<IDIYComponent<?>> selectedComponents = renderingContext.getSelectedComponents();
        Set<IDIYComponent<?>> lockedComponents = renderingContext.getLockedComponents();
        IComponentFilter componentFilter = renderingContext.getFilter();
        boolean isDragInProgress = renderingContext.isDragInProgress();
        Set<DrawingOption> drawingOptions = renderingContext.getDrawingOptions();
        
        List<IDIYComponent<?>> failedComponents = new ArrayList<IDIYComponent<?>>();
        
        for (IDIYComponent<?> component : components) {

            /*
             * Do not draw the component if it's filtered out.
             */
            if (componentFilter != null && !componentFilter.testComponent(component)) {
                continue;
            }

            ComponentState state = ComponentState.NORMAL;

            if (drawingOptions.contains(DrawingOption.SELECTION) && selectedComponents.contains(component)) {

                if (isDragInProgress) {
                    state = ComponentState.DRAGGING;
                } else {
                    state = ComponentState.SELECTED;
                }
            }

            /*
             * Do not track the area if component is not invalidated and was
             * drawn in the same state.
             */
            boolean trackArea = lastDrawnStateMap.get(component) != state;

            synchronized (graphics2d) {
                graphics2dWrapper.startedDrawingComponent();

                if (!trackArea) {
                    graphics2dWrapper.stopTracking();
                }

                /*
                 * Draw locked components in a new composite.
                 */
                if (lockedComponents.contains(component)) {
                    graphics2d.setComposite(lockedComposite);
                }

                /*
                 * Draw the component through the g2dWrapper.
                 */
                try {
                    component.draw(new GraphicsContext(graphics2dWrapper), state, drawingOptions.contains(DrawingOption.OUTLINE_MODE),
                            project, graphics2dWrapper);
                } catch (Exception e) {
                    LOG.error("Error drawing " + component.getName(), e);
                    failedComponents.add(component);
                }

                Area area = graphics2dWrapper.finishedDrawingComponent();

                if (trackArea && area != null && !area.isEmpty()) {
                    componentAreaMap.put(component, area);
                    lastDrawnStateMap.put(component, state);
                }
            }
        }
        
        return failedComponents;
    }

    @Override
    public void invalidateComponent(IDIYComponent<?> component) {
        componentAreaMap.remove(component);
        lastDrawnStateMap.remove(component);
    }

    @Override
    public List<IDIYComponent<?>> findComponentsAt(Point point, Project project) {
        List<IDIYComponent<?>> components = new ArrayList<IDIYComponent<?>>();
        
        for (int i = 0; i < project.getComponents().size(); i++) {
            Area area = componentAreaMap.get(project.getComponents().get(i));
            
            if (area != null && area.contains(point)) {
                components.add(0, project.getComponents().get(i));
            }
        }
        
        return components;
    }

    @Override
    public Area getComponentArea(IDIYComponent<?> component) {
        return componentAreaMap.get(component);
    }

    @Override
    public void clearComponentAreaMap() {
        componentAreaMap.clear();
        lastDrawnStateMap.clear();
    }
    
    @Override
    public Map<IDIYComponent<?>, Area> getComponentAreaMap() {
        return componentAreaMap;
    }

}
