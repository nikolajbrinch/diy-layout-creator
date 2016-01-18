package org.diylc.app.view.rendering;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.diylc.core.ComponentState;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.graphics.GraphicsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentSlotRenderer extends AbstractBasicRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentSlotRenderer.class);

    private static final Composite slotComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);

    @Override
    public List<IDIYComponent<?>> render(RenderingContext renderingContext) {
        Project project = renderingContext.getProject();
        Graphics2DWrapper graphics2dWrapper = renderingContext.getGraphics2dWrapper();
        Set<DrawingOption> drawingOptions = renderingContext.getDrawingOptions();
        List<IDIYComponent<?>> componentSlot = renderingContext.getComponentSlot();
        List<Point> controlPointSlot = renderingContext.getControlPointSlot();

        List<IDIYComponent<?>> failedComponents = new ArrayList<IDIYComponent<?>>();

        if (componentSlot != null) {
            /*
             * Draw component slot in a separate composite.
             */
            graphics2dWrapper.startedDrawingComponent();
            graphics2dWrapper.setComposite(slotComposite);

            for (IDIYComponent<?> component : componentSlot) {
                try {

                    component.draw(new GraphicsContext(graphics2dWrapper), ComponentState.NORMAL,
                            drawingOptions.contains(DrawingOption.OUTLINE_MODE), project, graphics2dWrapper);

                } catch (Exception e) {
                    LOG.error("Error drawing " + component.getName(), e);
                    failedComponents.add(component);
                }
            }

            graphics2dWrapper.finishedDrawingComponent();
        }
        
        if (controlPointSlot != null) {
            /*
             * Draw control points of the component in the slot.
             */
            for (Point point : controlPointSlot) {
                if (point != null) {
                    graphics2dWrapper.setColor(RenderingConstants.SELECTED_CONTROL_POINT_COLOR.darker());
                    graphics2dWrapper.fillOval(point.x - RenderingConstants.CONTROL_POINT_SIZE / 2, point.y
                            - RenderingConstants.CONTROL_POINT_SIZE / 2, RenderingConstants.CONTROL_POINT_SIZE,
                            RenderingConstants.CONTROL_POINT_SIZE);
                    graphics2dWrapper.setColor(RenderingConstants.SELECTED_CONTROL_POINT_COLOR);
                    graphics2dWrapper.fillOval(point.x - RenderingConstants.CONTROL_POINT_SIZE / 2 + 1, point.y
                            - RenderingConstants.CONTROL_POINT_SIZE / 2 + 1, RenderingConstants.CONTROL_POINT_SIZE - 2,
                            RenderingConstants.CONTROL_POINT_SIZE - 2);
                }
            }
        }

        return failedComponents;

    }

}
