package org.diylc.components.misc

import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.geom.Rectangle2D

import org.diylc.common.HorizontalAlignment
import org.diylc.common.Orientation
import org.diylc.common.VerticalAlignment
import org.diylc.components.AbstractComponent
import org.diylc.components.ComponentDescriptor
import org.diylc.core.ComponentState
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext

@ComponentDescriptor(name = "Label", author = "Branislav Stojkovic", category = "Misc", description = "User defined label", instanceNamePrefix = "L", zOrder = IDIYComponent.TEXT, flexibleZOrder = true, stretchable = false, bomPolicy = BomPolicy.NEVER_SHOW)
public class Label extends AbstractComponent<Void> {

    public static String DEFAULT_TEXT = "Double click to edit text"

    private static final long serialVersionUID = 1L

    private Point point = new Point(0, 0)
    private String text = DEFAULT_TEXT
    private Font font = LABEL_FONT
    private Color color = LABEL_COLOR
    @Deprecated
    private boolean center
    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER
    private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER
    private Orientation orientation = Orientation.DEFAULT

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState,
            boolean outlineMode, Project project,
            IDrawingObserver drawingObserver) {
        graphicsContext
                .setColor(componentState == ComponentState.SELECTED ? LABEL_COLOR_SELECTED
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
        graphicsContext.setColor(LABEL_COLOR)
        graphicsContext.setFont(LABEL_FONT.deriveFont((float) 13f * width / 32).deriveFont(
                Font.PLAIN))

        FontMetrics fontMetrics = graphicsContext.getFontMetrics()
        Rectangle2D rect = fontMetrics.getStringBounds("Abc", graphicsContext.graphics2D)

        int textHeight = (int) (rect.getHeight())
        int textWidth = (int) (rect.getWidth())

        // Center text horizontally and vertically.
        int x = (width - textWidth) / 2 + 1
        int y = (height - textHeight) / 2 + fontMetrics.getAscent()

        graphicsContext.drawString("Abc", x, y)
    }

    @EditableProperty
    public Font getFont() {
        return font
    }

    public void setFont(Font font) {
        this.font = font
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

    @EditableProperty
    public Orientation getOrientation() {
        if (orientation == null) {
            orientation = Orientation.DEFAULT
        }
        return orientation
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation
    }

    @EditableProperty(name = "Font Size")
    public int getFontSize() {
        return font.getSize()
    }

    public void setFontSize(int size) {
        font = font.deriveFont((float) size)
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

    @EditableProperty(defaultable = false)
    public String getText() {
        return text
    }

    public void setText(String text) {
        this.text = text
    }

    @EditableProperty
    public Color getColor() {
        return color
    }

    public void setColor(Color color) {
        this.color = color
    }

    @EditableProperty(name = "Vertical alignment")
    public VerticalAlignment getVerticalAlignment() {
        if (verticalAlignment == null) {
            verticalAlignment = VerticalAlignment.CENTER
        }
        return verticalAlignment
    }

    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment
    }

    @EditableProperty(name = "Horizontal alignment")
    public HorizontalAlignment getHorizontalAlignment() {
        if (horizontalAlignment == null) {
            horizontalAlignment = HorizontalAlignment.CENTER
        }
        return horizontalAlignment
    }

    public void setHorizontalAlignment(HorizontalAlignment alignment) {
        this.horizontalAlignment = alignment
    }

    @Override
    public String getName() {
        return super.getName()
    }

    @Override
    public Void getValue() {
        return null
    }

    @Override
    public void setValue(Void value) {
    }
}
