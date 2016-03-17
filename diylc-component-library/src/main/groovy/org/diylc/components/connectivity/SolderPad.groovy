package org.diylc.components.connectivity

import org.diylc.components.Colors

import java.awt.Color
import java.awt.Point

import org.diylc.components.AbstractComponent
import org.diylc.components.PCBLayer
import org.diylc.core.components.annotations.ComponentBomPolicy;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.annotations.ComponentLayer;
import org.diylc.core.components.annotations.ComponentPads;
import org.diylc.core.components.ComponentState
import org.diylc.core.components.annotations.ComponentEditOptions;
import org.diylc.core.components.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.components.VisibilityPolicy
import org.diylc.core.components.BomPolicy
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants

@ComponentEditOptions(stretchable = false)
@ComponentBomPolicy(BomPolicy.NEVER_SHOW)
@ComponentPads(false)
@ComponentLayer(IDIYComponent.PAD)
@ComponentDescriptor(name = "Solder Pad", category = "Connectivity", author = "Branislav Stojkovic", description = "Copper solder pad, round or square", instanceNamePrefix = "Pad")
public class SolderPad extends AbstractComponent {

    public static final String id = "066375ac-eb5b-4535-83d5-53c91b3f1462"
    
    private static final long serialVersionUID = 1L

    private static Size SIZE = new Size(0.09d, SizeUnit.in)
    
    private static Size HOLE_SIZE = new Size(0.8d, SizeUnit.mm)
    
    private static Color COLOR = Color.black

    private Point point = new Point(0, 0)

    @EditableProperty
    Size size = SIZE

    @EditableProperty(name = "Color")
    Color color = COLOR

    @EditableProperty
    Type type = Type.ROUND

    @EditableProperty(name = "Hole")
    Size holeSize = HOLE_SIZE

    @EditableProperty
    PCBLayer layer = PCBLayer._1

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState,
            boolean outlineMode, Project project,
            IDrawingObserver drawingObserver) {
        if (checkPointsClipped(graphicsContext.getClip())) {
            return
        }
        int diameter = getClosestOdd((int) getSize().convertToPixels())
        int holeDiameter = getClosestOdd((int) getHoleSize().convertToPixels())
        graphicsContext.setColor(componentState == ComponentState.SELECTED
                || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
                : color)

        int x = point.x - diameter.intdiv(2)
        int y = point.y - diameter.intdiv(2)

        if (type == Type.ROUND) {
            graphicsContext.fillOval(x, y, diameter, diameter)
        } else {
            graphicsContext.fillRect(x, y, diameter, diameter)
        }

        if (getHoleSize().getValue() > 0) {
            graphicsContext.setColor(Constants.CANVAS_COLOR)
            graphicsContext.fillOval(point.x - holeDiameter.intdiv(2), point.y - holeDiameter.intdiv(2), holeDiameter, holeDiameter)
        }
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int diameter = getClosestOdd(width / 2)
        int holeDiameter = 5
        graphicsContext.setColor(COLOR)
        graphicsContext.fillOval((int) (width - diameter) / 2, (int) (height - diameter) / 2, diameter, diameter)
        graphicsContext.setColor(Constants.CANVAS_COLOR)
        graphicsContext.fillOval((int) (width - holeDiameter) / 2, (int) (height - holeDiameter) / 2, holeDiameter, holeDiameter)
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

    static enum Type {
        ROUND, SQUARE

        @Override
        public String toString() {
            return name().substring(0, 1) + name().substring(1).toLowerCase()
        }
    }
}
