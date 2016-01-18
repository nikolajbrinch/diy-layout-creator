package org.diylc.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.diylc.core.ComponentState;
import org.diylc.core.Display;
import org.diylc.core.IDrawingObserver;
import org.diylc.core.ObjectCache;
import org.diylc.core.Project;
import org.diylc.core.Theme;
import org.diylc.core.VisibilityPolicy;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.config.Configuration;
import org.diylc.core.graphics.GraphicsContext;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;
import org.diylc.core.utils.Constants;

/**
 * Base class for all leaded components such as resistors or capacitors. Has two
 * control points and draws leads between them. Also, it positions and draws the
 * shape of the component as specified by a child class.
 * 
 * @author Branislav Stojkovic
 */
public abstract class AbstractLeadedComponent<T> extends
		AbstractTransparentComponent<T> {

	private static final long serialVersionUID = 1L;

	public static Size LEAD_THICKNESS = new Size(0.6d, SizeUnit.mm);
	public static Size DEFAULT_SIZE = new Size(1d, SizeUnit.in);

    @EditableProperty(name = "Length", defaultable = true)
	protected Size length;

	@EditableProperty(name = "Width", defaultable = true)
	protected Size width;
	
	protected Point[] points = new Point[] {
			new Point((int) (-DEFAULT_SIZE.convertToPixels() / 2), 0),
			new Point((int) (DEFAULT_SIZE.convertToPixels() / 2), 0) };
	
    @EditableProperty(name = "Color")
	protected Color bodyColor = Color.white;

	@EditableProperty(name = "Border")
	protected Color borderColor = Color.black;

	@EditableProperty(name = "Label color")
	protected Color labelColor = Colors.LABEL_COLOR;

    @EditableProperty(name = "Lead color")
	protected Color leadColor = Colors.DEFAULT_LEAD_COLOR;
    
    @EditableProperty
	protected Display display = Display.NAME;
    
	private boolean flipStanding = false;

	protected AbstractLeadedComponent() {
		super();
		try {
			this.length = getDefaultLength().clone();
			this.width = getDefaultWidth().clone();
		} catch (CloneNotSupportedException e) {
			// This should never happen because Size supports cloning.
		} catch (NullPointerException e) {
			// This will happen if components do not have any shape.
		}
	}

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState,
					 boolean outlineMode, Project project,
					 IDrawingObserver drawingObserver) {
		double distance = points[0].distance(points[1]);
		Shape shape = getBodyShape();
		// If there's no body, just draw the line connecting the ending points.
		if (shape == null) {
			drawLead(graphicsContext, componentState);
		} else if (supportsStandingMode()
				&& length.convertToPixels() > points[0].distance(points[1])) {
			// When ending points are too close draw the component in standing
			// mode.
			int width = getClosestOdd(this.width.convertToPixels());
			Shape body = new Ellipse2D.Double((getFlipStanding() ? points[1]
					: points[0]).x
					- width / 2, (getFlipStanding() ? points[1] : points[0]).y
					- width / 2, width, width);
			Composite oldComposite = graphicsContext.getComposite();
			if (alpha < Colors.MAX_ALPHA) {
				graphicsContext.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA));
			}
			graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR
					: getStandingBodyColor());
			graphicsContext.fill(body);
			graphicsContext.setComposite(oldComposite);
			Color finalBorderColor;
			if (outlineMode) {
				Theme theme = Configuration.INSTANCE.getTheme();

				finalBorderColor = componentState == ComponentState.SELECTED
						|| componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
						: theme.getOutlineColor();
			} else {
				finalBorderColor = componentState == ComponentState.SELECTED
						|| componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
						: borderColor;
			}

			graphicsContext.setColor(finalBorderColor);
			graphicsContext.draw(body);
			if (!outlineMode) {
				drawLead(graphicsContext, componentState);
			}
		} else {
			// Normal mode with component body in the center and two lead parts.
			Rectangle shapeRect = shape.getBounds();
			Double theta = Math.atan2(points[1].y - points[0].y, points[1].x
					- points[0].x);
			// Go back to the original transformation to draw leads.
			if (!outlineMode) {
				AffineTransform textTransform = graphicsContext.getTransform();
				// if (length.convertToPixels() > points[0].distance(points[1]))
				// {
				// g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				// 0.5f));
				// }
				int leadThickness = getClosestOdd(getLeadThickness());
				double leadLength = (distance - calculatePinSpacing(shapeRect))
						/ 2 - leadThickness / 2;
				graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(
						leadThickness));
				Color leadColor = shouldShadeLeads() ? getLeadColorForPainting(
						componentState).darker()
						: getLeadColorForPainting(componentState);
				graphicsContext.setColor(leadColor);
				int endX = (int) (points[0].x + Math.cos(theta) * leadLength);
				int endY = (int) Math.round(points[0].y + Math.sin(theta)
						* leadLength);
				graphicsContext.drawLine(points[0].x, points[0].y, endX, endY);
				endX = (int) (points[1].x + Math.cos(theta - Math.PI)
						* leadLength);
				endY = (int) Math.round(points[1].y + Math.sin(theta - Math.PI)
						* leadLength);
				graphicsContext.drawLine(points[1].x, points[1].y, endX, endY);
				if (shouldShadeLeads()) {
					graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(
							leadThickness - 2));
					leadColor = getLeadColorForPainting(componentState);
					graphicsContext.setColor(leadColor);
					graphicsContext.drawLine(points[0].x, points[0].y,
							(int) (points[0].x + Math.cos(theta) * leadLength),
							(int) (points[0].y + Math.sin(theta) * leadLength));
					graphicsContext.drawLine(points[1].x, points[1].y,
							(int) (points[1].x + Math.cos(theta - Math.PI)
									* leadLength), (int) (points[1].y + Math
									.sin(theta - Math.PI)
									* leadLength));
				}
				graphicsContext.setTransform(textTransform);
			}
			// Transform graphics to draw the body in the right place and at the
			// right angle.
			AffineTransform oldTransform = null;
			double width;
			double length;
			if (useShapeRectAsPosition()) {
				width = shapeRect.getHeight();
				length = shapeRect.getWidth();
			} else {
				width = getWidth().convertToPixels();
				length = getLength().convertToPixels();
			}
			oldTransform = graphicsContext.getTransform();
			graphicsContext.translate((points[0].x + points[1].x - length) / 2,
					(points[0].y + points[1].y - width) / 2);
			graphicsContext.rotate(theta, length / 2, width / 2);
			// Draw body.
			Composite oldComposite = graphicsContext.getComposite();
			if (alpha < Colors.MAX_ALPHA) {
				graphicsContext.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA));
			}
			if (bodyColor != null) {
				if (bodyColor != null) {
					graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR
							: bodyColor);
					graphicsContext.fill(shape);
				}
			}
			decorateComponentBody(graphicsContext, outlineMode);
			graphicsContext.setComposite(oldComposite);
			graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1));
			Color finalBorderColor;
			if (outlineMode) {
				Theme theme = Configuration.INSTANCE.getTheme();
				finalBorderColor = componentState == ComponentState.SELECTED
						|| componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
						: theme.getOutlineColor();
			} else {
				finalBorderColor = componentState == ComponentState.SELECTED
						|| componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
						: borderColor;
			}
			graphicsContext.setColor(finalBorderColor);
			graphicsContext.draw(shape);

			// // Go back to the original transformation to draw leads.
			// if (!outlineMode) {
			// AffineTransform textTransform = g2d.getTransform();
			// g2d.setTransform(oldTransform);
			// if (length.convertToPixels() > points[0].distance(points[1])) {
			// g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
			// 0.5f));
			// }
			// int leadThickness = getClosestOdd(getLeadThickness());
			// double leadLength = (distance - calculatePinSpacing(shapeRect)) /
			// 2 - leadThickness / 2;
			// g2d.setStroke(ObjectCache.getInstance().fetchBasicStroke(leadThickness));
			// Color leadColor = shouldShadeLeads() ?
			// getLeadColor(componentState).darker()
			// : getLeadColor(componentState);
			// g2d.setColor(leadColor);
			// int endX = (int) (points[0].x + Math.cos(theta) * leadLength);
			// int endY = (int) Math.round(points[0].y + Math.sin(theta) *
			// leadLength);
			// g2d.drawLine(points[0].x, points[0].y, endX, endY);
			// endX = (int) (points[1].x + Math.cos(theta - Math.PI) *
			// leadLength);
			// endY = (int) Math.round(points[1].y + Math.sin(theta - Math.PI) *
			// leadLength);
			// g2d.drawLine(points[1].x, points[1].y, endX, endY);
			// if (shouldShadeLeads()) {
			// g2d.setStroke(ObjectCache.getInstance().fetchBasicStroke(leadThickness
			// - 2));
			// leadColor = getLeadColor(componentState);
			// g2d.setColor(leadColor);
			// g2d.drawLine(points[0].x, points[0].y, (int) (points[0].x +
			// Math.cos(theta)
			// * leadLength), (int) (points[0].y + Math.sin(theta) *
			// leadLength));
			// g2d.drawLine(points[1].x, points[1].y, (int) (points[1].x +
			// Math.cos(theta
			// - Math.PI)
			// * leadLength), (int) (points[1].y + Math.sin(theta - Math.PI)
			// * leadLength));
			// }
			// g2d.setComposite(oldComposite);
			// g2d.setTransform(textTransform);
			// }

			// Draw label.
			graphicsContext.setFont(LABEL_FONT);
			if (useShapeRectAsPosition()) {
				graphicsContext.translate(shapeRect.x, shapeRect.y);
			}
			Color finalLabelColor;
			if (outlineMode) {
				Theme theme = Configuration.INSTANCE.getTheme();
				finalLabelColor = componentState == ComponentState.SELECTED
						|| componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED
						: theme.getOutlineColor();
			} else {
				finalLabelColor = componentState == ComponentState.SELECTED
						|| componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED
						: labelColor;
			}
			graphicsContext.setColor(finalLabelColor);
			FontMetrics fontMetrics = graphicsContext.getFontMetrics();
			String label = display == Display.NAME ? getName()
					: (getValue() == null ? "" : getValue().toString());
			Rectangle2D textRect = fontMetrics.getStringBounds(label, graphicsContext.graphics2D);
			// Don't offset in outline mode.
			int offset = outlineMode ? 0 : getLabelOffset((int) length,
					(int) width);
			// Adjust label angle if needed to make sure that it's readable.
			if ((theta >= Math.PI / 2 && theta <= Math.PI)
					|| (theta < -Math.PI / 2 && theta > -Math.PI)) {
				graphicsContext.rotate(Math.PI, length / 2, width / 2);
				offset = -offset;
			}
			graphicsContext.drawString(label, (int) (length - textRect.getWidth()) / 2
					+ offset, calculateLabelYCoordinate(shapeRect, textRect,
					fontMetrics));
			graphicsContext.setTransform(oldTransform);
		}
	}

	private void drawLead(GraphicsContext graphicsContext, ComponentState componentState) {
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(
				getLeadThickness()));
		Color leadColor = shouldShadeLeads() ? getLeadColorForPainting(
				componentState).darker()
				: getLeadColorForPainting(componentState);
		graphicsContext.setColor(leadColor);
		graphicsContext.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);
		if (shouldShadeLeads()) {
			graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(
					getLeadThickness() - 2));
			leadColor = getLeadColorForPainting(componentState);
			graphicsContext.setColor(leadColor);
			graphicsContext.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);
		}
	}

	protected void decorateComponentBody(GraphicsContext graphicsContext, boolean outlineMode) {
		// Do nothing.
	}

	protected int calculateLabelYCoordinate(Rectangle2D shapeRect,
			Rectangle2D textRect, FontMetrics fontMetrics) {
		return (int) (shapeRect.getHeight() - textRect.getHeight()) / 2
				+ fontMetrics.getAscent();
	}

	protected boolean shouldShadeLeads() {
		return true;
	}

	protected boolean supportsStandingMode() {
		return false;
	}

	protected int getLabelOffset(int bodyWidth, int labelWidth) {
		return 0;
	}

	/**
	 * @return default component length.
	 */
	protected abstract Size getDefaultLength();

	/**
	 * Returns default component width.
	 * 
	 * @return
	 */
	protected abstract Size getDefaultWidth();

	/**
	 * @return shape that represents component body. Shape should not be
	 *         transformed and should be referenced to (0, 0).
	 */
	protected abstract Shape getBodyShape();

	/**
	 * Controls how component shape should be placed relative to start and end
	 * point.
	 * 
	 * @return
	 *         <code>true<code> if shape rect should be used to center the component or <code>false</code>
	 *         to place the component relative to <code>length</code> and
	 *         <code>width</code> values.
	 */
	protected boolean useShapeRectAsPosition() {
		return true;
	}

	/**
	 * @return default lead thickness. Override this method to change it.
	 */
	protected int getLeadThickness() {
		return (int) LEAD_THICKNESS.convertToPixels();
	}

	/**
	 * @return default lead color. Override this method to change it.
	 */
	protected Color getLeadColorForPainting(ComponentState componentState) {
		return componentState == ComponentState.SELECTED
				|| componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
				: getLeadColor();
	}

	public Color getLeadColor() {
		if (leadColor == null) {
			leadColor = Colors.LEAD_COLOR_ICON;
		}
		return leadColor;
	}

	public void setLeadColor(Color leadColor) {
		this.leadColor = leadColor;
	}

	protected int calculatePinSpacing(Rectangle shapeRect) {
		return shapeRect.width;
	}

	@Override
	public int getControlPointCount() {
		return points.length;
	}

	@Override
	public Point getControlPoint(int index) {
		return (Point) points[index];
	}

	@Override
	public boolean isControlPointSticky(int index) {
		return true;
	}

	@Override
	public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
		return VisibilityPolicy.ALWAYS;
	}

	@Override
	public void setControlPoint(Point point, int index) {
		points[index].setLocation(point);
	}

	public Color getBodyColor() {
		return bodyColor;
	}

	public void setBodyColor(Color bodyColor) {
		this.bodyColor = bodyColor;
	}

	public Color getStandingBodyColor() {
		return bodyColor;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public Size getLength() {
		return length;
	}

	public void setLength(Size length) {
		this.length = length;
	}

	public Size getWidth() {
		return width;
	}

	public void setWidth(Size width) {
		this.width = width;
	}

	public Display getDisplay() {
		return display;
	}

	public void setDisplay(Display display) {
		this.display = display;
	}

	public Color getLabelColor() {
		return labelColor;
	}

	public void setLabelColor(Color labelColor) {
		this.labelColor = labelColor;
	}

	/**
	 * Override this method with @EditableProperty annotation in child classes
	 * where standing mode is supported
	 * 
	 * @return
	 */
	public boolean getFlipStanding() {
		return flipStanding;
	}

	public void setFlipStanding(boolean flipStanding) {
		this.flipStanding = flipStanding;
	}
}
