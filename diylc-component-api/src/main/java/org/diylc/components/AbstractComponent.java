package org.diylc.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.diylc.core.HorizontalAlignment;
import org.diylc.core.IDIYComponent;
import org.diylc.core.VerticalAlignment;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.components.ComponentModel;
import org.diylc.core.components.DefaultComponentModel;
import org.diylc.core.graphics.GraphicsContext;
import org.diylc.core.platform.Platform;
import org.diylc.core.serialization.JsonReader;
import org.diylc.core.serialization.JsonWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

import groovy.transform.AutoClone;

/**
 * Abstract implementation of {@link IDIYComponent} that contains component name
 * and toString.
 * 
 * IMPORTANT: to improve performance, all fields except for <code>Point</code>
 * and <code>Point</code> arrays should be immutable. Failing to comply with
 * this can result in annoying and hard to trace bugs.
 * 
 * @author Branislav Stojkovic
 * 
 * @param <T>
 */
public abstract class AbstractComponent implements IDIYComponent {

    private static final long serialVersionUID = 1L;

    protected static final Font LABEL_FONT = new Font(Platform.getPlatform().getDefaultTextFontName(), Font.PLAIN, 14);

    private String id;

    private ComponentModel componentModel = null;

    @EditableProperty(defaultable = false)
    private String name = "";

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public ComponentModel getComponentModel() {
        return componentModel;
    }

    @Override
    public void setComponentModel(ComponentModel componentModel) {
        this.componentModel = componentModel;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean canControlPointOverlap(int index) {
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getValueForDisplay() {
        return "";
    }

    /**
     * Returns the closest odd number, i.e. x when x is odd, or x + 1 when x is
     * even.
     * 
     * @param x
     * @return
     */
    protected int getClosestOdd(double x) {
        return ((int) x / 2) * 2 + 1;
    }

    /**
     * @param clip
     * @return true if none of the control points lie in the clip rectangle.
     */
    protected boolean checkPointsClipped(Shape clip) {
        for (int i = 0; i < getControlPointCount(); i++) {
            if (clip.contains(getControlPoint(i))) {
                return false;
            }
        }
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int i = 0; i < getControlPointCount(); i++) {
            Point p = getControlPoint(i);
            if (minX > p.x) {
                minX = p.x;
            }
            if (maxX < p.x) {
                maxX = p.x;
            }
            if (minY > p.y) {
                minY = p.y;
            }
            if (maxY < p.y) {
                maxY = p.y;
            }
        }
        Rectangle2D rect = new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);

        return !clip.intersects(rect);
    }

    protected void drawCenteredText(GraphicsContext graphicsContext, String text, Point point, HorizontalAlignment horizontalAlignment,
            VerticalAlignment verticalAlignment) {
        drawCenteredText(graphicsContext, text, point, horizontalAlignment, verticalAlignment, Angle._0);
    }

    protected void drawCenteredText(GraphicsContext graphicsContext, String text, Point point, HorizontalAlignment horizontalAlignment,
            VerticalAlignment verticalAlignment, Angle angle) {
        AffineTransform transform = graphicsContext.getTransform();

        FontMetrics fontMetrics = graphicsContext.getFontMetrics();
        Rectangle stringBounds = fontMetrics.getStringBounds(text, graphicsContext.graphics2D).getBounds();

        Font font = graphicsContext.getFont();
        FontRenderContext renderContext = graphicsContext.getFontRenderContext();
        GlyphVector glyphVector = font.createGlyphVector(renderContext, text);
        Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();

        int textX = 0;
        switch (horizontalAlignment) {
        case CENTER:
            textX = point.x - stringBounds.width / 2;
            break;
        case LEFT:
            textX = point.x;
            break;
        case RIGHT:
            textX = point.x - stringBounds.width;
            break;
        }

        int textY = 0;
        switch (verticalAlignment) {
        case TOP:
            textY = point.y + stringBounds.height;
            break;
        case CENTER:
            textY = point.y - visualBounds.height / 2 - visualBounds.y;
            break;
        case BOTTOM:
            textY = point.y - visualBounds.y;
            break;
        }

        double rotation = angle.getAngle();
        AffineTransform rotate = AffineTransform.getRotateInstance(rotation, point.x, point.y);
        graphicsContext.graphics2D.transform(rotate);
        graphicsContext.drawString(text, textX, textY);
        graphicsContext.setTransform(transform);
    }

    protected void drawArea(GraphicsContext graphicsContext, int x, int y, Area area, Color color, Color borderColor) {
        Area transformedArea = new Area(area);
        AffineTransform move = AffineTransform.getTranslateInstance(x, y);
        transformedArea.transform(move);

        if (color != null) {
            graphicsContext.setColor(color);
            graphicsContext.fill(transformedArea);
        }
        if (borderColor != null) {
            graphicsContext.setColor(borderColor);
            graphicsContext.draw(transformedArea);
        }
    }

    @Override
    public boolean equalsTo(IDIYComponent other) {
        if (other == null) {
            return false;
        }

        if (!other.getClass().equals(this.getClass())) {
            return false;
        }

        Class<?> clazz = this.getClass();

        while (AbstractComponent.class.isAssignableFrom(clazz)) {
            Field[] fields = clazz.getDeclaredFields();
            clazz = clazz.getSuperclass();

            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(this);
                        Object otherValue = field.get(other);

                        if (!compareObjects(value, otherValue)) {
                            return false;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return true;
    }

    private boolean compareObjects(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        }

        if (o1 == null || o2 == null) {
            return false;
        }

        if (o1.getClass().isArray()) {
            return Arrays.equals((Object[]) o1, (Object[]) o2);
        }

        return o1.equals(o2);
    }

}
