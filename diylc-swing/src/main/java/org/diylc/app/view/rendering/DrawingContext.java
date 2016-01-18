package org.diylc.app.view.rendering;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Set;

import org.diylc.components.IComponentFilter;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;

public class DrawingContext {

    private final Graphics2D graphics2d;
    
    private final Project project;
    
    private final Set<DrawingOption> drawOptions;
    
    private final IComponentFilter filter;
    
    private final Rectangle selectionRect;
    
    private final List<IDIYComponent<?>> selectedComponents;
    
    private final Set<IDIYComponent<?>> lockedComponents;
    
    private final Set<IDIYComponent<?>> groupedComponents;
    
    private final List<Point> controlPointSlot;
    
    private final List<IDIYComponent<?>> componentSlot;
    
    private final boolean dragInProgress;

    public DrawingContext(Graphics2D graphics2d, Project project, Set<DrawingOption> drawOptions, IComponentFilter filter, Rectangle selectionRect,
            List<IDIYComponent<?>> selectedComponents, Set<IDIYComponent<?>> lockedComponents, Set<IDIYComponent<?>> groupedComponents,
            List<Point> controlPointSlot, List<IDIYComponent<?>> componentSlot, boolean dragInProgress) {
        this.graphics2d = graphics2d;
        this.project = project;
        this.drawOptions = drawOptions;
        this.filter = filter;
        this.selectionRect = selectionRect;
        this.selectedComponents = selectedComponents;
        this.lockedComponents = lockedComponents;
        this.groupedComponents = groupedComponents;
        this.controlPointSlot = controlPointSlot;
        this.componentSlot = componentSlot;
        this.dragInProgress = dragInProgress;
    }

    public DrawingContext(DrawingContext drawingContext) {
        this(drawingContext.graphics2d, drawingContext.project, drawingContext.drawOptions, 
                drawingContext.filter, drawingContext.selectionRect, drawingContext.selectedComponents, 
                drawingContext.lockedComponents, drawingContext.groupedComponents, drawingContext.controlPointSlot,
                drawingContext.componentSlot, drawingContext.dragInProgress);        
    }

    public Graphics2D getGraphics2d() {
        return graphics2d;
    }

    public Project getProject() {
        return project;
    }

    public Set<DrawingOption> getDrawingOptions() {
        return drawOptions;
    }

    public IComponentFilter getFilter() {
        return filter;
    }

    public Rectangle getSelectionRect() {
        return selectionRect;
    }

    public List<IDIYComponent<?>> getSelectedComponents() {
        return selectedComponents;
    }

    public Set<IDIYComponent<?>> getLockedComponents() {
        return lockedComponents;
    }

    public Set<IDIYComponent<?>> getGroupedComponents() {
        return groupedComponents;
    }

    public List<Point> getControlPointSlot() {
        return controlPointSlot;
    }

    public List<IDIYComponent<?>> getComponentSlot() {
        return componentSlot;
    }

    public boolean isDragInProgress() {
        return dragInProgress;
    }
}