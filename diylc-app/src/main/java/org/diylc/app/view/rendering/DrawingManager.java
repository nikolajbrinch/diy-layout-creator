package org.diylc.app.view.rendering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.diylc.app.EventType;
import org.diylc.app.MessageDispatcher;
import org.diylc.core.IDIYComponent;
import org.diylc.core.ObjectCache;
import org.diylc.core.Project;
import org.diylc.core.Theme;
import org.diylc.core.config.Configuration;
import org.diylc.core.utils.Constants;

/**
 * Utility that deals with painting {@link Project} on the {@link Graphics2D}
 * and keeps areas taken by each drawn component.
 * 
 * @author Branislav Stojkovic
 */
public class DrawingManager {

    public static final String ZOOM_KEY = "zoom";

    public static String DEBUG_COMPONENT_AREAS = "org.diylc.debugComponentAreas";

    private Renderer canvasRenderer = new CanvasRenderer();

    private Renderer gridRenderer = new GridRenderer();

    private Renderer componentsRenderer = new ComponentsRenderer();

    private Renderer controlPointsRenderer = new ControlPointsRenderer();

    private Renderer componentSlotRenderer = new ComponentSlotRenderer();

    private Renderer selectionRenderer = new SelectionRenderer();

    private Theme theme = Configuration.INSTANCE.getTheme();

    private double zoomLevel = 1d;

    private MessageDispatcher<EventType> messageDispatcher;

    private boolean debugComponentAreas;

    public DrawingManager(MessageDispatcher<EventType> messageDispatcher) {
        this.messageDispatcher = messageDispatcher;

        String debugComponentAreasStr = System.getProperty(DEBUG_COMPONENT_AREAS);
        debugComponentAreas = debugComponentAreasStr != null && debugComponentAreasStr.equalsIgnoreCase("true");
    }

    /**
     * Paints the project onto the canvas and returns the list of components
     * that failed to draw.
     * 
     * @param drawingContext
     *            TODO
     * 
     * @return
     */
    public List<IDIYComponent> drawProject(DrawingContext drawingContext) {
        List<IDIYComponent> failedComponents = new ArrayList<IDIYComponent>();

        Project project = drawingContext.getProject();

        if (project != null) {
            /*
             * TODO: Zoom should be calculated when zooomlevel is set, so we don't calculate on rendering
             */
            Set<DrawingOption> drawingOptions = drawingContext.getDrawingOptions();

            double zoom = 1d;

            if (drawingOptions.contains(DrawingOption.ZOOM)) {
                zoom = zoomLevel;
            } else {
                zoom = 1d / Constants.PIXEL_SIZE;
            }

            Graphics2D graphics2d = drawingContext.getGraphics2d();
            
            /*
             * TODO: Canvas dimensions should be calculated elsewhere
             */
            Dimension canvasDimensions = calculateCanvasDimensions(drawingContext.getProject(), zoom, true);
            Graphics2DWrapper graphics2dWrapper = new Graphics2DWrapper(graphics2d);

            /*
             * Create rendering context
             */
            RenderingContext renderingContext = RenderingContext.newInstance(drawingContext, zoom, canvasDimensions, graphics2dWrapper,
                    theme);

            applyDrawingOptions(renderingContext);
            
            failedComponents.addAll(canvasRenderer.render(renderingContext));
            failedComponents.addAll(gridRenderer.render(renderingContext));

            if (Math.abs(1.0 - zoom) > 1e-4) {
                graphics2dWrapper.scale(zoom, zoom);
            }

            failedComponents.addAll(componentsRenderer.render(renderingContext));
            failedComponents.addAll(controlPointsRenderer.render(renderingContext));
            failedComponents.addAll(componentSlotRenderer.render(renderingContext));
            failedComponents.addAll(selectionRenderer.render(renderingContext));

            if (debugComponentAreas) {
                drawDebugComponentAreas(graphics2d);
            }
        }
        
        return failedComponents;
    }

    private void drawDebugComponentAreas(Graphics2D graphics2d) {
        graphics2d.setStroke(ObjectCache.getInstance().fetchBasicStroke(1));
        graphics2d.setColor(Color.red);

        for (Area area : componentsRenderer.getComponentAreaMap().values()) {
            graphics2d.draw(area);
        }
    }

    private void applyDrawingOptions(RenderingContext renderingContext) {
        Graphics2D graphics2D = renderingContext.getGraphics2d();
        Set<DrawingOption> drawingOptions = renderingContext.getDrawingOptions();
        
        if (drawingOptions.contains(DrawingOption.ANTIALIASING)) {
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        if (Configuration.INSTANCE.getHiQualityRender()) {
            graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        } else {
            graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
            graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        }
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(double zoomLevel) {
        this.zoomLevel = zoomLevel;
        fireZoomChanged();
    }

    public void invalidateComponent(IDIYComponent component) {
        componentsRenderer.invalidateComponent(component);
    }

    public Area getComponentArea(IDIYComponent component) {
        return componentsRenderer.getComponentArea(component);
    }

    public void clearComponentAreaMap() {
        componentsRenderer.clearComponentAreaMap();
    }

    public List<IDIYComponent> findComponentsAt(Point point, Project project) {
        return componentsRenderer.findComponentsAt(point, project);
    }

    public Dimension calculateCanvasDimensions(Project project, Double zoomLevel, boolean useZoom) {
        double width = project.getWidth().convertToPixels();
        double height = project.getHeight().convertToPixels();
        
        if (useZoom) {
            width *= zoomLevel;
            height *= zoomLevel;
        } else {
            width /= Constants.PIXEL_SIZE;
            height /= Constants.PIXEL_SIZE;
        }
        
        return new Dimension((int) width, (int) height);
    }

    public void fireZoomChanged() {
        messageDispatcher.dispatchMessage(EventType.ZOOM_CHANGED, zoomLevel);
        messageDispatcher.dispatchMessage(EventType.REPAINT);
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
        Configuration.INSTANCE.setTheme(theme);
        messageDispatcher.dispatchMessage(EventType.REPAINT);
    }
}
