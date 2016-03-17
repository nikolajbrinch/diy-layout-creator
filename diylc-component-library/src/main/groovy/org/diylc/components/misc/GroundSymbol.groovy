package org.diylc.components.misc

import java.awt.Color
import java.awt.Point
import java.awt.Polygon

import org.diylc.components.AbstractComponent
import org.diylc.core.components.annotations.ComponentBomPolicy;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.annotations.ComponentPads;
import org.diylc.core.components.ComponentState
import org.diylc.core.components.annotations.ComponentEditOptions
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache
import org.diylc.core.Project
import org.diylc.core.components.VisibilityPolicy
import org.diylc.core.components.BomPolicy
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentBomPolicy(BomPolicy.NEVER_SHOW)
@ComponentEditOptions(stretchable = false)
@ComponentPads(false)
@ComponentDescriptor(name = "Ground Symbol", author = "Branislav Stojkovic", category = "Schematics", instanceNamePrefix = "GND", description = "Ground schematic symbol")
class GroundSymbol extends AbstractComponent {

    public static final String id = "9670c1e9-b509-4c1d-ab06-d0db4561d4ac"

    private static final long serialVersionUID = 1L

    private static Color COLOR = Color.black

    private static Size SIZE = new Size(0.15d, SizeUnit.in)

    private Point point = new Point(0, 0)

    Void value = null

    @EditableProperty
    Color color = COLOR

    @EditableProperty
    Size size = SIZE

    @EditableProperty(name = "Style")
    GroundSymbolType type = GroundSymbolType.DEFAULT

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode,
            Project project, IDrawingObserver drawingObserver) {
        int sizePx = (int) size.convertToPixels()
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
        graphicsContext.setColor(color)
        int x = point.x
        int y = point.y
        graphicsContext.drawLine(x, y, x, y + sizePx / 6)
        if (type == GroundSymbolType.DEFAULT) {
            int delta = sizePx / 7
            for (int i = 0; i < 5; i++) {
                graphicsContext.drawLine(x - sizePx / 2 + delta * i, y + sizePx / 6 * (i + 1), x + sizePx / 2
                        - delta * i, y + sizePx / 6 * (i + 1))
            }
        } else {
            Polygon poly = new Polygon([x - sizePx / 2, x + sizePx / 2, x ] as int[], [y + sizePx / 6, y + sizePx / 6, y + sizePx ] as int[], 3)
            graphicsContext.draw(poly)
        }
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int margin = 3 * width / 32
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
        graphicsContext.setColor(COLOR)
        graphicsContext.drawLine(width / 2, margin * 2, width / 2, margin * 3 + height / 5)
        for (int i = 0; i < 5; i++) {
            graphicsContext.drawLine(margin * (i + 1), margin * (3 + i) + height / 5, width - margin * (i + 1),
                    margin * (3 + i) + height / 5)
        }
    }

    @Override
    public Point getControlPoint(int index) {
        return point
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
    public void setControlPoint(Point point, int index) {
        this.point = point
    }

    @Override
    public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
        return VisibilityPolicy.WHEN_SELECTED
    }

    public static enum GroundSymbolType {
        DEFAULT("Default"), TRIANGLE("Triangle")

        private String title

        private GroundSymbolType(String title) {
            this.title = title
        }

        public String getTitle() {
            return title
        }

        @Override
        public String toString() {
            return title
        }
    }
}
