package org.diylc.app.view.rendering;

import java.awt.Dimension;

import org.diylc.core.Theme;

public class RenderingContext extends DrawingContext {

    private final double zoom;
    
    private final Dimension canvasDimensions;

    private final Graphics2DWrapper graphics2dWrapper;

    private Theme theme;

    public RenderingContext(DrawingContext drawingContext, double zoom, Dimension canvasDimensions, Graphics2DWrapper graphics2dWrapper, Theme theme) {
        super(drawingContext);
        this.zoom = zoom;
        this.canvasDimensions = canvasDimensions;
        this.graphics2dWrapper = graphics2dWrapper;
        this.setTheme(theme);
    }

    public static RenderingContext newInstance(DrawingContext drawingContext, double zoom, Dimension canvasDimensions,
            Graphics2DWrapper graphics2dWrapper, Theme theme) {
        return new RenderingContext(drawingContext, zoom, canvasDimensions, graphics2dWrapper, theme);
    }

    public double getZoom() {
        return zoom;
    }

    public Dimension getCanvasDimensions() {
        return canvasDimensions;
    }

    public Graphics2DWrapper getGraphics2dWrapper() {
        return graphics2dWrapper;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }    

}
