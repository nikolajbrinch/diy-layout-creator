package org.diylc.app.view.rendering;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.diylc.core.GridType;
import org.diylc.core.components.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.Theme;
import org.diylc.core.measures.Size;

public class GridRenderer extends AbstractBasicRenderer {

    @Override
    public List<IDIYComponent> render(RenderingContext renderingContext) {
        Project project = renderingContext.getProject();
        Graphics2D graphics2d = renderingContext.getGraphics2d();
        Graphics2DWrapper graphics2dWrapper = renderingContext.getGraphics2dWrapper();
        Theme theme = renderingContext.getTheme();
        Size gridSpacing = project.getGridSpacing();
        Dimension canvasDimensions = renderingContext.getCanvasDimensions();
        double zoom = renderingContext.getZoom();
        Set<DrawingOption> drawingOptions = renderingContext.getDrawingOptions();

        GridType gridType = GridType.LINES;

        if (drawingOptions.contains(DrawingOption.GRID) && gridType != GridType.NONE) {
            double zoomStep = gridSpacing.convertToPixels() * zoom;

            if (gridType == GridType.CROSSHAIR) {
                graphics2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[] {
                        (float) zoomStep / 2, (float) zoomStep / 2 }, (float) zoomStep / 4));
            } else if (gridType == GridType.DOT) {
                graphics2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[] { 1f,
                        (float) zoomStep - 1 }, 0f));
            }

            graphics2dWrapper.setColor(theme.getGridColor());

            for (double i = zoomStep; i < canvasDimensions.width; i += zoomStep) {
                graphics2dWrapper.drawLine((int) i, 0, (int) i, canvasDimensions.height - 1);
            }

            for (double j = zoomStep; j < canvasDimensions.height; j += zoomStep) {
                graphics2dWrapper.drawLine(0, (int) j, canvasDimensions.width - 1, (int) j);
            }
        }
        
        return Collections.emptyList();
    }


}
