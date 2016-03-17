package org.diylc.app.view.rendering;

import java.awt.Point;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.diylc.core.components.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.components.VisibilityPolicy;

public class ControlPointsRenderer extends AbstractBasicRenderer {

    @Override
    public List<IDIYComponent> render(RenderingContext renderingContext) {
        Project project = renderingContext.getProject();
        Graphics2DWrapper graphics2dWrapper = renderingContext.getGraphics2dWrapper();
        List<IDIYComponent> components = project.getComponents();
        List<IDIYComponent> selectedComponents = renderingContext.getSelectedComponents();
        Set<IDIYComponent> groupedComponents = renderingContext.getGroupedComponents();
        boolean isDragInProgress = renderingContext.isDragInProgress();
        Set<DrawingOption> drawingOptions = renderingContext.getDrawingOptions();

        /*
         * Draw control points.
         */
        if (drawingOptions.contains(DrawingOption.CONTROL_POINTS)) {

            /*
             * Draw unselected points first to make sure they are below.
             */
            if (isDragInProgress || drawingOptions.contains(DrawingOption.OUTLINE_MODE)) {
                for (IDIYComponent component : components) {
                    for (int i = 0; i < component.getControlPointCount(); i++) {
                        VisibilityPolicy visibilityPolicy = component.getControlPointVisibilityPolicy(i);
                        if ((groupedComponents.contains(component)
                                && (visibilityPolicy == VisibilityPolicy.ALWAYS || (selectedComponents.contains(component) && visibilityPolicy == VisibilityPolicy.WHEN_SELECTED)) || (!groupedComponents
                                .contains(component) && !selectedComponents.contains(component) && component
                                    .getControlPointVisibilityPolicy(i) == VisibilityPolicy.ALWAYS))) {
                            graphics2dWrapper.setColor(RenderingConstants.CONTROL_POINT_COLOR);
                            Point controlPoint = component.getControlPoint(i);
                            int pointSize = RenderingConstants.CONTROL_POINT_SIZE - 2;
                            graphics2dWrapper
                                    .fillOval(controlPoint.x - pointSize / 2, controlPoint.y - pointSize / 2, pointSize, pointSize);
                        }
                    }
                }
            }

            /*
             * Then draw the selected ones.
             */
            for (IDIYComponent component : selectedComponents) {
                for (int i = 0; i < component.getControlPointCount(); i++) {
                    if (!groupedComponents.contains(component)
                            && (component.getControlPointVisibilityPolicy(i) == VisibilityPolicy.WHEN_SELECTED || component
                                    .getControlPointVisibilityPolicy(i) == VisibilityPolicy.ALWAYS)) {

                        Point controlPoint = component.getControlPoint(i);
                        int pointSize = RenderingConstants.CONTROL_POINT_SIZE;

                        graphics2dWrapper.setColor(RenderingConstants.SELECTED_CONTROL_POINT_COLOR.darker());
                        graphics2dWrapper.fillOval(controlPoint.x - pointSize / 2, controlPoint.y - pointSize / 2, pointSize, pointSize);
                        graphics2dWrapper.setColor(RenderingConstants.SELECTED_CONTROL_POINT_COLOR);
                        graphics2dWrapper.fillOval(controlPoint.x - RenderingConstants.CONTROL_POINT_SIZE / 2 + 1, controlPoint.y
                                - RenderingConstants.CONTROL_POINT_SIZE / 2 + 1, RenderingConstants.CONTROL_POINT_SIZE - 2,
                                RenderingConstants.CONTROL_POINT_SIZE - 2);
                    }
                }
            }
        }

        return Collections.emptyList();
    }

}
