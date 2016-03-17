package org.diylc.components.misc

import org.diylc.components.Colors

import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Point
import java.awt.geom.Rectangle2D

import org.diylc.components.AbstractComponent
import org.diylc.components.Geometry
import org.diylc.core.components.annotations.ComponentAutoEdit;
import org.diylc.core.components.annotations.ComponentBomPolicy;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.annotations.ComponentLayer;
import org.diylc.core.components.ComponentState
import org.diylc.core.components.annotations.ComponentEditOptions;
import org.diylc.core.HorizontalAlignment
import org.diylc.core.components.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Orientation
import org.diylc.core.Project
import org.diylc.core.VerticalAlignment
import org.diylc.core.components.VisibilityPolicy
import org.diylc.core.components.BomPolicy
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.graphics.GraphicsContext

@ComponentAutoEdit
@ComponentBomPolicy(BomPolicy.NEVER_SHOW)
@ComponentEditOptions(stretchable = false)
@ComponentLayer(value = IDIYComponent.TEXT, flexible = true)
@ComponentDescriptor(name = "Label", author = "Branislav Stojkovic", category = "Misc", description = "User defined label", instanceNamePrefix = "L")
public class Label extends AbstractComponent implements Geometry {

    public static final String id = "be45857e-5b18-4549-92a1-fe21d0dea9f7"
    
    private static final long serialVersionUID = 1L

    private static String DEFAULT_TEXT = "Double click to edit text"

    private Point point = new Point(0, 0)

    @Deprecated
    private boolean center

    Void value = null

    @EditableProperty(defaultable = false)
    String text = DEFAULT_TEXT

    @EditableProperty
    Font font = LABEL_FONT

    @EditableProperty
    Color color = Colors.LABEL_COLOR

    @EditableProperty(name = "Horizontal alignment")
    HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER

    @EditableProperty(name = "Vertical alignment")
    VerticalAlignment verticalAlignment = VerticalAlignment.CENTER

    @EditableProperty
    Orientation orientation = Orientation.DEFAULT

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState,
            boolean outlineMode, Project project,
            IDrawingObserver drawingObserver) {
        graphicsContext
                .setColor(componentState == ComponentState.SELECTED ? Colors.LABEL_COLOR_SELECTED
                : color)
        graphicsContext.setFont(font)
        FontMetrics fontMetrics = graphicsContext.getFontMetrics()
        Rectangle2D rect = fontMetrics.getStringBounds(text, graphicsContext.graphics2D)

        int textHeight = (int) rect.getHeight()
        int textWidth = (int) rect.getWidth()

        int x = point.x
        int y = point.y
        switch (getVerticalAlignment()) {
            case VerticalAlignment.CENTER:
                y = point.y - textHeight / 2 + fontMetrics.getAscent()
                break
            case VerticalAlignment.TOP:
                y = point.y - textHeight + fontMetrics.getAscent()
                break
            case VerticalAlignment.BOTTOM:
                y = point.y + fontMetrics.getAscent()
                break
            default:
                throw new RuntimeException("Unexpected alignment: "
                + getVerticalAlignment())
        }
        switch (getHorizontalAlignment()) {
            case HorizontalAlignment.CENTER:
                x = point.x - textWidth / 2
                break
            case HorizontalAlignment.LEFT:
                x = point.x
                break
            case HorizontalAlignment.RIGHT:
                x = point.x - textWidth
                break
            default:
                throw new RuntimeException("Unexpected alignment: "
                + getHorizontalAlignment())
        }

        switch (getOrientation()) {
            case Orientation._90:
                graphicsContext.rotate(Math.PI / 2, point.x, point.y)
                break
            case Orientation._180:
                graphicsContext.rotate(Math.PI, point.x, point.y)
                break
            case Orientation._270:
                graphicsContext.rotate(Math.PI * 3 / 2, point.x, point.y)
                break
        }
        graphicsContext.drawString(text, x, y)
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        graphicsContext.setColor(Colors.LABEL_COLOR)
        graphicsContext.setFont(LABEL_FONT.deriveFont(toFloat(13f * width / 32)).deriveFont(Font.PLAIN))

        FontMetrics fontMetrics = graphicsContext.getFontMetrics()
        Rectangle2D rect = fontMetrics.getStringBounds("Abc", graphicsContext.graphics2D)

        int textHeight = (int) (rect.getHeight())
        int textWidth = (int) (rect.getWidth())

        // Center text horizontally and vertically.
        int x = (width - textWidth) / 2 + 1
        int y = (height - textHeight) / 2 + fontMetrics.getAscent()

        graphicsContext.drawString("Abc", x, y)
    }

    // Bold and italic fields are named to be alphabetically after Font. This is
    // important!

    @EditableProperty(name = "Font Bold")
    public boolean getBold() {
        return font.isBold()
    }

    public void setBold(boolean bold) {
        if (bold) {
            if (font.isItalic()) {
                font = font.deriveFont(Font.BOLD + Font.ITALIC)
            } else {
                font = font.deriveFont(Font.BOLD)
            }
        } else {
            if (font.isItalic()) {
                font = font.deriveFont(Font.ITALIC)
            } else {
                font = font.deriveFont(Font.PLAIN)
            }
        }
    }

    @EditableProperty(name = "Font Italic")
    public boolean getItalic() {
        return font.isItalic()
    }

    public void setItalic(boolean italic) {
        if (italic) {
            if (font.isBold()) {
                font = font.deriveFont(Font.BOLD + Font.ITALIC)
            } else {
                font = font.deriveFont(Font.ITALIC)
            }
        } else {
            if (font.isBold()) {
                font = font.deriveFont(Font.BOLD)
            } else {
                font = font.deriveFont(Font.PLAIN)
            }
        }
    }

    @EditableProperty(name = "Font Size")
    public int getFontSize() {
        return font.getSize()
    }

    public void setFontSize(int size) {
        font = font.deriveFont(toFloat(size))
    }

    @Override
    public int getControlPointCount() {
        return 1
    }

    @Override
    public Point getControlPoint(int index) {
        return point
    }

    @Override
    public boolean isControlPointSticky(int index) {
        return false
    }

    @Override
    public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
        return VisibilityPolicy.WHEN_SELECTED
    }

    @Override
    public void setControlPoint(Point point, int index) {
        this.point.setLocation(point)
    }

    @Override
    public String getName() {
        return super.getName()
    }

}
