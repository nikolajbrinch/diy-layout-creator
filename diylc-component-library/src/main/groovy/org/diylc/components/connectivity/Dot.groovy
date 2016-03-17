package org.diylc.components.connectivity

import org.diylc.components.Colors

import java.awt.Color
import java.awt.Point

import org.diylc.components.AbstractComponent
import org.diylc.core.components.annotations.ComponentBomPolicy;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.ComponentState
import org.diylc.core.components.annotations.ComponentEditOptions
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.components.VisibilityPolicy
import org.diylc.core.components.BomPolicy
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentEditOptions(stretchable = false)
@ComponentBomPolicy(BomPolicy.NEVER_SHOW)
@ComponentDescriptor(name = "Dot", category = "Connectivity", author = "Branislav Stojkovic", description = "Connector dot", instanceNamePrefix = "Dot")
public class Dot extends AbstractComponent {

    public static final String id = "edbb23e2-bd6e-49aa-9734-882ad6da0fd4"

    private static final long serialVersionUID = 1L

    private static Size SIZE = new Size(1d, SizeUnit.mm)

    private static Color COLOR = Color.black

    private Point point = new Point(0, 0)

    @EditableProperty(name = "Color")
    Color color = COLOR

    @EditableProperty
    Size size = SIZE

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState,
            boolean outlineMode, Project project,
            IDrawingObserver drawingObserver) {
        if (checkPointsClipped(graphicsContext.getClip())) {
            return
        }
        int diameter = getClosestOdd((int) getSize().convertToPixels())
        graphicsContext.setColor(componentState == ComponentState.SELECTED
                || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
                : color)
        graphicsContext.fillOval(point.x - diameter.intdiv(2), point.y - diameter.intdiv(2), diameter,
                diameter)
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int diameter = 7 * width / 32
        graphicsContext.setColor(COLOR)
        graphicsContext.fillOval((width - diameter) / 2, (height - diameter) / 2, diameter,
                diameter)
    }

    @Override
    public String getName() {
        return super.getName()
    }

    @Override
    public int getControlPointCount() {
        return 1
    }

    @Override
    public boolean isControlPointSticky(int index) {
        return true
    }

    @Override
    public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
        return VisibilityPolicy.NEVER
    }

    @Override
    public Point getControlPoint(int index) {
        return point
    }

    @Override
    public void setControlPoint(Point point, int index) {
        this.point.setLocation(point)
    }
}
