package org.diylc.components.electromechanical;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.diylc.common.ObjectCache;
import org.diylc.common.Orientation;
import org.diylc.components.AbstractTransparentComponent;
import org.diylc.components.ComponentDescriptor;
import org.diylc.core.ComponentState;
import org.diylc.core.IDIYComponent;
import org.diylc.core.IDrawingObserver;
import org.diylc.core.Project;
import org.diylc.core.Theme;
import org.diylc.core.VisibilityPolicy;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.config.Configuration;
import org.diylc.core.graphics.GraphicsContext;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;
import org.diylc.utils.Constants;

@ComponentDescriptor(name = "9V Battery Snap", category = "Electromechanical", author = "Branislav Stojkovic", description = "", stretchable = false, zOrder = IDIYComponent.COMPONENT, instanceNamePrefix = "BTR", autoEdit = false)
public class BatterySnap9V extends AbstractTransparentComponent<String> {

	private static final long serialVersionUID = 1L;

	private static Color BODY_COLOR = Color.darkGray;
	private static Size WIDTH = new Size(0.5d, SizeUnit.in);
	private static Size LENGTH = new Size(0.75d, SizeUnit.in);
	private static Size TERMINAL_DIAMETER = new Size(0.3d, SizeUnit.in);
	private static Size TERMINAL_SPACING = new Size(0.5d, SizeUnit.in);
	private static Size TERMINAL_BORDER = new Size(0.7d, SizeUnit.mm);

	private String value = "";
	private Point controlPoint = new Point(0, 0);
	transient Shape[] body;
	private Orientation orientation = Orientation.DEFAULT;
	private Color color = BODY_COLOR;

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode,
					 Project project, IDrawingObserver drawingObserver) {
		Shape[] body = getBody();

		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1));
		if (componentState != ComponentState.DRAGGING) {
			Composite oldComposite = graphicsContext.getComposite();
			if (alpha < MAX_ALPHA) {
				graphicsContext.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha
						/ MAX_ALPHA));
			}
			graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : color);
			graphicsContext.fill(body[0]);
			graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : METAL_COLOR);
			graphicsContext.fill(body[1]);
			graphicsContext.fill(body[2]);
			graphicsContext.setComposite(oldComposite);
		}

		Color finalBorderColor;
		if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme();
			finalBorderColor = componentState == ComponentState.SELECTED
					|| componentState == ComponentState.DRAGGING ? SELECTION_COLOR : theme
					.getOutlineColor();
		} else {
			finalBorderColor = componentState == ComponentState.SELECTED
					|| componentState == ComponentState.DRAGGING ? SELECTION_COLOR : color
					.brighter();
		}

		graphicsContext.setColor(finalBorderColor);
		graphicsContext.draw(body[0]);
		graphicsContext.setColor(METAL_COLOR.darker());
		graphicsContext.draw(body[1]);
		graphicsContext.setColor(METAL_COLOR.darker());
		graphicsContext.draw(body[2]);
	}

	public Shape[] getBody() {
		if (body == null) {
			body = new Shape[3];

			int x = controlPoint.x;
			int y = controlPoint.y;
			int width = (int) WIDTH.convertToPixels();
			int length = (int) LENGTH.convertToPixels();
			int totalLength = length + width / 2;
			int terminalDiameter = (int) TERMINAL_DIAMETER.convertToPixels();
			int terminalSpacing = (int) TERMINAL_SPACING.convertToPixels();
			int terminalBorder = (int) TERMINAL_BORDER.convertToPixels();

			Area mainArea = new Area(new Rectangle2D.Double(x, y - width / 2, length, width));
			mainArea.add(new Area(new Ellipse2D.Double(x + length - width / 2, y - width / 2,
					width, width)));

			body[0] = mainArea;

			Area terminalArea = new Area(new Ellipse2D.Double(x + (totalLength - terminalSpacing)
					/ 2 - terminalDiameter / 2, y - terminalDiameter / 2, terminalDiameter,
					terminalDiameter));

			int centerX = x + (totalLength + terminalSpacing) / 2;
			int[] terminalX = new int[6];
			int[] terminalY = new int[6];

			for (int i = 0; i < 6; i++) {
				terminalX[i] = (int) (centerX + Math.cos(Math.PI / 3 * i) * terminalDiameter / 2);
				terminalY[i] = (int) (y + Math.sin(Math.PI / 3 * i) * terminalDiameter / 2);
			}
			terminalArea.add(new Area(new Polygon(terminalX, terminalY, 6)));

			body[1] = terminalArea;

			terminalArea = new Area(new Ellipse2D.Double(x + (totalLength - terminalSpacing) / 2
					- terminalDiameter / 2 + terminalBorder, y - terminalDiameter / 2
					+ terminalBorder, terminalDiameter - 2 * terminalBorder, terminalDiameter - 2
					* terminalBorder));

			for (int i = 0; i < 6; i++) {
				terminalX[i] = (int) (centerX + Math.cos(Math.PI / 3 * i)
						* (terminalDiameter / 2 + terminalBorder));
				terminalY[i] = (int) (y + Math.sin(Math.PI / 3 * i)
						* (terminalDiameter / 2 + terminalBorder));
			}
			terminalArea.add(new Area(new Polygon(terminalX, terminalY, 6)));

			body[2] = terminalArea;

			// Rotate if needed
			if (orientation != Orientation.DEFAULT) {
				double theta = 0;
				switch (orientation) {
				case _90:
					theta = Math.PI / 2;
					break;
				case _180:
					theta = Math.PI;
					break;
				case _270:
					theta = Math.PI * 3 / 2;
					break;
				}
				AffineTransform rotation = AffineTransform.getRotateInstance(theta, x, y);
				for (Shape shape : body) {
					Area area = (Area) shape;
					area.transform(rotation);
				}
			}
		}
		return body;
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		graphicsContext.setColor(BODY_COLOR);
		graphicsContext.fillOval(width / 4, width / 32, width / 2, width / 2);
		graphicsContext.setColor(BODY_COLOR.darker());
		graphicsContext.drawOval(width / 4, width / 32, width / 2, width / 2);
		graphicsContext.setColor(BODY_COLOR);
		graphicsContext.fillRect(width / 4, width / 32 + width / 4, width / 2, width * 3 / 4 - 2 * width / 32);
		graphicsContext.setColor(BODY_COLOR.darker());
		graphicsContext.drawRect(width / 4, width / 32 + width / 4, width / 2, width * 3 / 4 - 2 * width / 32);
		graphicsContext.setColor(METAL_COLOR);
		graphicsContext.fillOval(width / 4 + 3 * width / 32, width / 32 + 3 * width / 32, width / 2 - 6 * width
				/ 32, width / 2 - 6 * width / 32);

		int centerX = width / 2;
		int centerY = height * 7 / 9;
		int terminalDiameter = width / 2 - 4 * width / 32;
		int[] terminalX = new int[6];
		int[] terminalY = new int[6];

		for (int i = 0; i < 6; i++) {
			terminalX[i] = (int) (centerX + Math.cos(Math.PI / 3 * i) * terminalDiameter / 2);
			terminalY[i] = (int) (centerY + Math.sin(Math.PI / 3 * i) * terminalDiameter / 2);
		}
		graphicsContext.fillPolygon(terminalX, terminalY, 6);
		graphicsContext.setColor(METAL_COLOR.darker());
		graphicsContext.drawOval(width / 4 + 3 * width / 32, width / 32 + 3 * width / 32, width / 2 - 6 * width
				/ 32, width / 2 - 6 * width / 32);
		graphicsContext.drawPolygon(terminalX, terminalY, 6);
	}

	@Override
	public int getControlPointCount() {
		return 1;
	}

	@Override
	public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
		return VisibilityPolicy.WHEN_SELECTED;
	}

	@Override
	public boolean isControlPointSticky(int index) {
		return true;
	}

	@Override
	public Point getControlPoint(int index) {
		return controlPoint;
	}

	@Override
	public void setControlPoint(Point point, int index) {
		this.controlPoint.setLocation(point);
		// Invalidate the body
		body = null;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@EditableProperty
	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
		// Invalidate the body
		body = null;
	}

	@EditableProperty
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
