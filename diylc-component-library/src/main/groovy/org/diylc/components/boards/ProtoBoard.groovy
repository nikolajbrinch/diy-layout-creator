package org.diylc.components.boards

import org.diylc.components.Colors
import org.diylc.components.AbstractBoard
import org.diylc.components.Geometry
import org.diylc.core.components.annotations.ComponentBomPolicy;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.annotations.ComponentLayer;
import org.diylc.core.components.ComponentState
import org.diylc.core.components.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.components.BomPolicy
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants

import java.awt.*


@ComponentBomPolicy(BomPolicy.SHOW_ONLY_TYPE_NAME)
@ComponentLayer(IDIYComponent.BOARD)
@ComponentDescriptor(name = "Prototype Board w/ Pads", category = "Boards", author = "Nikolaj Brinch Jørgensen", instanceNamePrefix = "Board", description = "Perforated board with solder pads")
public class ProtoBoard extends AbstractBoard implements Geometry {

    public static final String id = "c40cb14d-d6ac-49eb-9a9b-5083f09f74e8"
    
    private static final long serialVersionUID = 1L

    public static Color SILVER_COLOR = Color.decode("#C0C0C0")
    
    public static Size SPACING = new Size(0.1d, SizeUnit.in)

    public static Size PAD_SIZE = new Size(0.07d, SizeUnit.in)
    
    public static Size HOLE_SIZE = new Size(1.33d, SizeUnit.mm)
    
    
    @EditableProperty
    Size spacing = SPACING

    @EditableProperty(name = "Pad color")
    Color padColor = SILVER_COLOR

    public ProtoBoard() {
        super()
        this.boardColor = Colors.PCB_GREEN_COLOR
        this.borderColor = Colors.PCB_GREEN_BORDER_COLOR
    }
    
    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode, Project project, IDrawingObserver drawingObserver) {
        Shape clip = graphicsContext.getClip()

        if (checkPointsClipped(clip)
        && !clip.contains(firstPoint.x, secondPoint.y)
        && !clip.contains(secondPoint.x, firstPoint.y)) {
            return
        }

        super.draw(graphicsContext, componentState, outlineMode, project, drawingObserver)

        if (componentState != ComponentState.DRAGGING) {
            Point p = point(firstPoint)
            int diameter = getClosestOdd((int) PAD_SIZE.convertToPixels())
            int holeDiameter = getClosestOdd((int) HOLE_SIZE.convertToPixels())
            int spacing = (int) this.spacing.convertToPixels()

            graphicsContext.with {
                if (alpha < Colors.MAX_ALPHA) {
                    AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, toFloat(alpha / Colors.MAX_ALPHA))
                    setComposite(composite)
                }

                while (p.y < secondPoint.y - spacing) {
                    p.@x = firstPoint.x as int
                    p.@y += spacing

                    while (p.x < secondPoint.x - spacing - diameter) {
                        p.@x += spacing

                        drawFilledOval(p.x - diameter / 2, p.y - diameter / 2, diameter, padColor.darker(), padColor)
                        drawFilledOval(p.x - holeDiameter / 2, p.y - holeDiameter / 2, holeDiameter, padColor.darker(), Constants.CANVAS_COLOR)
                    }
                }
            }
            
            drawCoordinates(graphicsContext, spacing)
        }
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int factor = 32 / width

        graphicsContext.drawFilledRect( 2 / factor, width -  4 / factor, Colors.PCB_GREEN_BORDER_COLOR, Colors.PCB_GREEN_COLOR)
        graphicsContext.drawFilledOval(width / 4,  width / 2, SILVER_COLOR.darker(), SILVER_COLOR)
        graphicsContext.drawFilledOval(width / 2 - 2 / factor, getClosestOdd(5.0 / factor), SILVER_COLOR.darker(), Constants.CANVAS_COLOR)
    }
}
