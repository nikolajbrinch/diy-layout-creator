package org.diylc.components;

import org.diylc.common.HorizontalAlignment;
import org.diylc.common.VerticalAlignment;
import org.diylc.core.IDIYComponent;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.graphics.GraphicsContext;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

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
public abstract class AbstractComponent<T> implements IDIYComponent<T> {

	private static final long serialVersionUID = 1L;

	protected String name = "";

	public static Color SELECTION_COLOR = Color.red;
	public static Color LABEL_COLOR = Color.black;
	public static Color LABEL_COLOR_SELECTED = Color.red;
	public static Font LABEL_FONT = new Font("Tahoma", Font.PLAIN, 14);
	public static Color METAL_COLOR = Color.decode("#236B8E");

	@EditableProperty(defaultable = false)
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
	
	@Override
	public String getValueForDisplay() {
		return getValue() == null ? "" : getValue().toString();
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

	protected void drawCenteredText(GraphicsContext graphicsContext, String text, int x, int y,
			HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
		FontMetrics fontMetrics = graphicsContext.getFontMetrics();
		Rectangle stringBounds = fontMetrics.getStringBounds(text, graphicsContext.graphics2D).getBounds();

		Font font = graphicsContext.getFont();
		FontRenderContext renderContext = graphicsContext.getFontRenderContext();
		GlyphVector glyphVector = font.createGlyphVector(renderContext, text);
		Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();

		int textX = 0;
		switch (horizontalAlignment) {
		case CENTER:
			textX = x - stringBounds.width / 2;
			break;
		case LEFT:
			textX = x;
			break;
		case RIGHT:
			textX = x - stringBounds.width;
			break;
		}

		int textY = 0;
		switch (verticalAlignment) {
		case TOP:
			textY = y + stringBounds.height;
			break;
		case CENTER:
			textY = y - visualBounds.height / 2 - visualBounds.y;
			break;
		case BOTTOM:
			textY = y - visualBounds.y;
			break;
		}

		graphicsContext.drawString(text, textX, textY);
	}

	@SuppressWarnings("unchecked")
	public IDIYComponent<T> clone() throws CloneNotSupportedException {
		try {
			/*
			 * Instantiate object of the same type
			 */
			AbstractComponent<T> newInstance = (AbstractComponent<T>) this.getClass()
					.getConstructors()[0].newInstance();
			Class<?> clazz = this.getClass();
			while (AbstractComponent.class.isAssignableFrom(clazz)) {
				Field[] fields = clazz.getDeclaredFields();
				clazz = clazz.getSuperclass();
				// fields = this.getClass().getDeclaredFields();

				/*
				 * Copy over all non-static, non-final fields that are declared
				 * in AbstractComponent or one of it's child classes
				 */
				for (Field field : fields) {
					if (!Modifier.isStatic(field.getModifiers())
							&& !Modifier.isFinal(field.getModifiers())) {
						field.setAccessible(true);
						Object value = field.get(this);

						/*
						 * Deep copy point arrays.
						 * TODO: something nicer
						 */
						if (value != null
								&& value.getClass().isArray()
								&& value.getClass().getComponentType()
										.isAssignableFrom(Point.class)) {
							Object newArray = Array.newInstance(
									value.getClass().getComponentType(), Array.getLength(value));
							for (int i = 0; i < Array.getLength(value); i++) {
								Point p = (Point) Array.get(value, i);
								Array.set(newArray, i, new Point(p));
							}
							value = newArray;
						}

						/*
						 * Deep copy points.
						 * TODO: something nicer
						 */
						if (value != null && value instanceof Point) {
							value = new Point((Point) value);
						}

						field.set(newInstance, value);
					}
				}
			}
			return newInstance;
		} catch (Exception e) {
			throw new CloneNotSupportedException("Could not clone the component. Reason: "
					+ e.getMessage());
		}
	}

	@Override
	public boolean equalsTo(IDIYComponent<?> other) {
		if (other == null)
			return false;
		if (!other.getClass().equals(this.getClass()))
			return false;
		Class<?> clazz = this.getClass();
		while (AbstractComponent.class.isAssignableFrom(clazz)) {
			Field[] fields = clazz.getDeclaredFields();
			clazz = clazz.getSuperclass();
			// fields = this.getClass().getDeclaredFields();
			// Copy over all non-static, non-final fields that are declared
			// in
			// AbstractComponent or one of it's child classes
			for (Field field : fields) {
				if (!Modifier.isStatic(field.getModifiers())
						&& !Modifier.isFinal(field.getModifiers())) {
					field.setAccessible(true);
					try {
						Object value = field.get(this);
						Object otherValue = field.get(other);
						if (!compareObjects(value, otherValue))
							return false;
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return true;
	}

	private boolean compareObjects(Object o1, Object o2) {
		if (o1 == null && o2 == null)
			return true;
		if (o1 == null || o2 == null)
			return false;
		if (o1.getClass().isArray()) {
			return Arrays.equals((Object[])o1, (Object[])o2);
		}
		return o1.equals(o2);
	}
}
