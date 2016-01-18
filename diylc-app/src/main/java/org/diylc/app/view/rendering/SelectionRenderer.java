package org.diylc.app.view.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.diylc.core.IDIYComponent;
import org.diylc.core.utils.Constants;

public class SelectionRenderer extends AbstractBasicRenderer {

    @Override
    public List<IDIYComponent<?>> render(RenderingContext renderingContext) {
        Graphics2D graphics2d = renderingContext.getGraphics2d();
        Set<DrawingOption> drawingOptions = renderingContext.getDrawingOptions();
        Rectangle selectionRectangle = renderingContext.getSelectionRect();

        if (drawingOptions.contains(DrawingOption.SELECTION) && (selectionRectangle != null)) {
            graphics2d.setColor(Color.white);
            graphics2d.draw(selectionRectangle);
            graphics2d.setColor(Color.black);
            graphics2d.setStroke(Constants.DASHED_STROKE);
            graphics2d.draw(selectionRectangle);
        }

        return Collections.emptyList();
    }

}
