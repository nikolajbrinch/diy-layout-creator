package org.diylc.app.view.rendering;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

import org.diylc.core.components.IDIYComponent;
import org.diylc.core.Theme;

public class CanvasRenderer extends AbstractBasicRenderer {

    @Override
    public List<IDIYComponent> render(RenderingContext renderingContext) {
        Dimension canvasDimensions = renderingContext.getCanvasDimensions();
        Theme theme = renderingContext.getTheme();
        Graphics2DWrapper graphics2dWrapper = renderingContext.getGraphics2dWrapper();
        Graphics2D graphics2d = renderingContext.getGraphics2d();
        
        graphics2dWrapper.setColor(theme.getBgColor());
        graphics2dWrapper.fillRect(0, 0, canvasDimensions.width, canvasDimensions.height);
        graphics2d.clip(new Rectangle(new Point(0, 0), canvasDimensions));
        
        return Collections.emptyList();
    }

}
