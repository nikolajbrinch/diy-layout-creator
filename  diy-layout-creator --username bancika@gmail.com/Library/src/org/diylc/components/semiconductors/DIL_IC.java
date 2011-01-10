package org.diylc.components.semiconductors;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.diylc.common.Orientation;
import org.diylc.components.AbstractTransparentComponent;
import org.diylc.core.ComponentState;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.annotations.ComponentDescriptor;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;
import org.diylc.utils.Constants;

@ComponentDescriptor(name = "DIL IC", author = "Branislav Stojkovic", category = "Semiconductors", instanceNamePrefix = "IC", description = "test", stretchable = false, zOrder = IDIYComponent.COMPONENT)
public class DIL_IC extends AbstractTransparentComponent<String> {

	private static final long serialVersionUID = 1L;

	public static Color BODY_COLOR = Color.gray;
	public static Color BORDER_COLOR = Color.gray.darker();
	public static Color PIN_COLOR = Color.decode("#00B2EE");
	public static Color PIN_BORDER_COLOR = PIN_COLOR.darker();
	public static Color INDENT_COLOR = Color.lightGray;
	public static Color LABEL_COLOR = Color.white;
	public static Color LABEL_COLOR_SELECTED = Color.red;
	public static int EDGE_RADIUS = 6;
	public static Size PIN_SIZE = new Size(0.04d, SizeUnit.in);
	public static Size INDENT_SIZE = new Size(0.15d, SizeUnit.in);

	private String name = "New Component";
	private String value = "";
	private Orientation orientation = Orientation.DEFAULT;
	private PinCount pinCount = PinCount._8;
	private Size rowSpacing = new Size(0.3d, SizeUnit.in);
	private Point[] controlPoints = new Point[] { new Point(0, 0),
			new Point(0, (int) Constants.GRID), new Point(0, (int) (2 * Constants.GRID)),
			new Point(0, (int) (3 * Constants.GRID)), new Point((int) (3 * Constants.GRID), 0),
			new Point((int) (3 * Constants.GRID), (int) Constants.GRID),
			new Point((int) (3 * Constants.GRID), (int) (2 * Constants.GRID)),
			new Point((int) (3 * Constants.GRID), (int) (3 * Constants.GRID)) };
	private Shape body;

	public DIL_IC() {
		super();
		updateControlPoints();
	}

	@EditableProperty(defaultable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@EditableProperty
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@EditableProperty
	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
		// Reset body shape;
		body = null;
	}

	@EditableProperty(name = "Pins")
	public PinCount getPinCount() {
		return pinCount;
	}

	public void setPinCount(PinCount pinCount) {
		this.pinCount = pinCount;
		updateControlPoints();
		// Reset body shape;
		body = null;
	}

	@EditableProperty(name = "Row spacing")
	public Size getRowSpacing() {
		return rowSpacing;
	}

	public void setRowSpacing(Size rowSpacing) {
		this.rowSpacing = rowSpacing;
		updateControlPoints();
		// Reset body shape;
		body = null;
	}

	@Override
	public int getControlPointCount() {
		return controlPoints.length;
	}

	@Override
	public Point getControlPoint(int index) {
		return controlPoints[index];
	}

	@Override
	public void setControlPoint(Point point, int index) {
		controlPoints[index].setLocation(point);
		body = null;
	}

	private void updateControlPoints() {
		Point firstPoint = controlPoints[0];
		controlPoints = new Point[pinCount.getValue()];
		controlPoints[0] = firstPoint;
		// Update control points.
		int dx1;
		int dy1;
		int dx2;
		int dy2;
		for (int i = 0; i < pinCount.getValue() / 2; i++) {
			switch (orientation) {
			case DEFAULT:
				dx1 = 0;
				dy1 = (int) (i * Constants.GRID);
				dx2 = rowSpacing.convertToPixels();
				dy2 = (int) (i * Constants.GRID);
				break;
			case _90:
				dx1 = (int) (-i * Constants.GRID);
				dy1 = 0;
				dx2 = (int) (-i * Constants.GRID);
				dy2 = rowSpacing.convertToPixels();
				break;
			case _180:
				dx1 = 0;
				dy1 = (int) (-i * Constants.GRID);
				dx2 = -rowSpacing.convertToPixels();
				dy2 = (int) (-i * Constants.GRID);
				break;
			case _270:
				dx1 = (int) (i * Constants.GRID);
				dy1 = 0;
				dx2 = (int) (i * Constants.GRID);
				dy2 = rowSpacing.convertToPixels();
				break;
			default:
				throw new RuntimeException("Unexpected orientation: " + orientation);
			}
			controlPoints[i] = new Point(firstPoint.x + dx1, firstPoint.y + dy1);
			controlPoints[i + pinCount.getValue() / 2] = new Point(firstPoint.x + dx2, firstPoint.y
					+ dy2);
		}
	}

	public Shape getBody() {
		if (body == null) {
			int x = controlPoints[0].x;
			int y = controlPoints[0].y;
			int width;
			int height;
			int pinSize = PIN_SIZE.convertToPixels();
			switch (orientation) {
			case DEFAULT:
				width = rowSpacing.convertToPixels() - pinSize;
				height = (int) ((pinCount.getValue() / 2) * Constants.GRID);
				x += pinSize / 2;
				y -= (int) (Constants.GRID / 2);
				break;
			case _90:
				width = (int) ((pinCount.getValue() / 2) * Constants.GRID);
				height = rowSpacing.convertToPixels() - pinSize;
				x -= (int) (Constants.GRID / 2) + width - Constants.GRID;
				y += pinSize / 2;
				break;
			case _180:
				width = rowSpacing.convertToPixels() - pinSize;
				height = (int) ((pinCount.getValue() / 2) * Constants.GRID);
				x -= rowSpacing.convertToPixels() - pinSize / 2;
				y -= (int) (Constants.GRID / 2) + height - Constants.GRID;
				break;
			case _270:
				width = (int) ((pinCount.getValue() / 2) * Constants.GRID);
				height = rowSpacing.convertToPixels() - pinSize;
				x -= (int) (Constants.GRID / 2);
				y += pinSize / 2;
				break;
			default:
				throw new RuntimeException("Unexpected orientation: " + orientation);
			}
			body = new RoundRectangle2D.Double(x, y, width, height, EDGE_RADIUS, EDGE_RADIUS);
		}
		return body;
	}

	@Override
	public void draw(Graphics2D g2d, ComponentState componentState, Project project) {
		int pinSize = PIN_SIZE.convertToPixels() / 2 * 2;
		for (Point point : controlPoints) {
			g2d.setColor(PIN_COLOR);
			g2d.fillRect(point.x - pinSize / 2, point.y - pinSize / 2, pinSize, pinSize);
			g2d.setColor(PIN_BORDER_COLOR);
			g2d.drawRect(point.x - pinSize / 2, point.y - pinSize / 2, pinSize, pinSize);
		}
		if (componentState != ComponentState.DRAGGING) {
			Composite oldComposite = g2d.getComposite();
			if (alpha < MAX_ALPHA) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha
						/ MAX_ALPHA));
			}
			g2d.setColor(BODY_COLOR);
			g2d.fill(getBody());
			g2d.setComposite(oldComposite);
		}
		g2d.setColor(BORDER_COLOR);
		g2d.setStroke(new BasicStroke());
		g2d.draw(getBody());
		// Draw label.
		g2d.setFont(Constants.LABEL_FONT);
		switch (componentState) {
		case DRAGGING:
			g2d.setColor(BORDER_COLOR);
			break;
		case SELECTED:
			g2d.setColor(LABEL_COLOR_SELECTED);
			break;
		default:
			g2d.setColor(LABEL_COLOR);
		}
		FontMetrics fontMetrics = g2d.getFontMetrics(g2d.getFont());
		Rectangle2D rect = fontMetrics.getStringBounds(getName(), g2d);
		int textHeight = (int) (rect.getHeight());
		int textWidth = (int) (rect.getWidth());
		// Center text horizontally and vertically
		Rectangle bounds = getBody().getBounds();
		int x = bounds.x + (bounds.width - textWidth) / 2;
		int y = bounds.y + (bounds.height - textHeight) / 2 + fontMetrics.getAscent();
		g2d.drawString(getName(), x, y);
	}

	@Override
	public void drawIcon(Graphics2D g2d, int width, int height) {
		int radius = 6 * width / 32;
		g2d.setColor(BODY_COLOR);
		g2d.fillRoundRect(width / 6, 1, 4 * width / 6, height - 4, radius, radius);
		g2d.setColor(BORDER_COLOR);
		g2d.drawRoundRect(width / 6, 1, 4 * width / 6, height - 4, radius, radius);
		int pinSize = 2 * width / 32;
		g2d.setColor(PIN_COLOR);
		for (int i = 0; i < 4; i++) {
			g2d.fillRect(width / 6 - pinSize, (height / 5) * (i + 1) - 1, pinSize, pinSize);
			g2d.fillRect(5 * width / 6 + 1, (height / 5) * (i + 1) - 1, pinSize, pinSize);
		}
	}

	static enum PinCount {

		_4, _8, _10, _12, _14, _16, _24, _32;

		@Override
		public String toString() {
			return name().replace("_", "");
		}

		public int getValue() {
			return Integer.parseInt(toString());
		}
	}
}
