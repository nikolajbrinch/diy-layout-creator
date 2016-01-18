package org.diylc.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Point;

import org.diylc.core.ComponentState;
import org.diylc.core.HorizontalAlignment;
import org.diylc.core.IDrawingObserver;
import org.diylc.core.ObjectCache;
import org.diylc.core.Project;
import org.diylc.core.VerticalAlignment;
import org.diylc.core.VisibilityPolicy;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.graphics.GraphicsContext;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;

public abstract class AbstractBoard extends AbstractTransparentComponent<String> {

    private static final long serialVersionUID = 1L;

    public static float COORDINATE_FONT_SIZE = 9f;
    public static Size DEFAULT_WIDTH = new Size(1.5d, SizeUnit.in);
    public static Size DEFAULT_HEIGHT = new Size(1.2d, SizeUnit.in);

    protected String value = "";
    protected Point[] controlPoints = new Point[]{
            new Point(0, 0),
            new Point((int) DEFAULT_WIDTH.convertToPixels(), (int) DEFAULT_HEIGHT.convertToPixels())
    };
    protected Point firstPoint = new Point();
    protected Point secondPoint = new Point();

    protected Color boardColor = Colors.PCB_BOARD_COLOR;
    protected Color borderColor = Colors.PCB_BORDER_COLOR;
    protected Color coordinateColor = Colors.COORDINATE_COLOR;
    protected Boolean drawCoordinates = true;

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode,
                     Project project, IDrawingObserver drawingObserver) {
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1));

        if (componentState != ComponentState.DRAGGING) {
            Composite oldComposite = graphicsContext.getComposite();

            if (alpha < Colors.MAX_ALPHA) {
                graphicsContext.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA));
            }

            graphicsContext.setColor(boardColor);
            graphicsContext.fillRect(firstPoint.x, firstPoint.y, secondPoint.x - firstPoint.x, secondPoint.y - firstPoint.y);
            graphicsContext.setComposite(oldComposite);
        }

		/* 
		 * Do not track any changes that follow because the whole board has been
		 * tracked so far.
		 */
        drawingObserver.stopTracking();
        graphicsContext.setColor(componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : borderColor);
        graphicsContext.drawRect(firstPoint.x, firstPoint.y, secondPoint.x - firstPoint.x, secondPoint.y - firstPoint.y);
    }

    protected void drawCoordinates(GraphicsContext graphicsContext, int spacing) {
        if (!getDrawCoordinates()) {
            return;
        }

        Point p = new Point(firstPoint);
        graphicsContext.setColor(coordinateColor);
        graphicsContext.setFont(LABEL_FONT.deriveFont(COORDINATE_FONT_SIZE));

        int t = 1;
        while (p.y < secondPoint.y - spacing) {
            p.y += spacing;

            drawCenteredText(graphicsContext, getCoordinateLabel(t++), new Point(p.x + 2, p.y), HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
        }

        p = new Point(firstPoint);
        t = 1;

        while (p.x < secondPoint.x - spacing) {
            p.x += spacing;

            drawCenteredText(graphicsContext, getCoordinateLabel(t++), new Point(p.x, p.y - 2), HorizontalAlignment.CENTER, VerticalAlignment.TOP);
        }
    }

    private String getCoordinateLabel(int coordinate) {
        String result = "";
        while (coordinate > 0) {
            int digit = coordinate % 26;
            coordinate /= 26;
            if (digit == 0) {
                result = 'Z' + result;
                coordinate--;
            } else {
                result = (char) ((int) 'A' + digit - 1) + result;
            }
        }
        return result;
    }

    @EditableProperty(name = "Color")
    public Color getBoardColor() {
        return boardColor;
    }

    public void setBoardColor(Color boardColor) {
        this.boardColor = boardColor;
    }

    @EditableProperty(name = "Coordinate Color")
    public Color getCoordinateColor() {
        // Null protection for older files
        return coordinateColor == null ? Colors.COORDINATE_COLOR : coordinateColor;
    }

    public void setCoordinateColor(Color coordinateColor) {
        this.coordinateColor = coordinateColor;
    }

    @EditableProperty(name = "Border")
    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    @EditableProperty(name = "Draw Coordinates")
    public boolean getDrawCoordinates() {
        // Null protection for older files
        return drawCoordinates == null || drawCoordinates;
    }

    public void setDrawCoordinates(boolean drawCoordinates) {
        this.drawCoordinates = drawCoordinates;
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
    public boolean isControlPointSticky(int index) {
        return false;
    }

    @Override
    public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
        return VisibilityPolicy.WHEN_SELECTED;
    }

    @Override
    public void setControlPoint(Point point, int index) {
        controlPoints[index].setLocation(point);

        firstPoint.setLocation(Math.min(controlPoints[0].x, controlPoints[1].x), Math.min(controlPoints[0].y, controlPoints[1].y));
        secondPoint.setLocation(Math.max(controlPoints[0].x, controlPoints[1].x), Math.max(controlPoints[0].y, controlPoints[1].y));
    }

    @EditableProperty
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
