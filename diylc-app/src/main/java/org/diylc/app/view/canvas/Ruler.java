package org.diylc.app.view.canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import org.diylc.core.platform.Platform;

/**
 * {@link JComponent} that renders ruler. It features configurable units (cm or
 * in), orientation and ability to indicate cursor position.
 * 
 * @author Branislav Stojkovic
 */
public class Ruler extends JComponent {

    private static final long serialVersionUID = 1L;

    public static final Color COLOR = Color.decode("#C0FF3E");

    public static final Color SELECTION_COLOR = Color.blue;

    public static final int PIXELS_PER_INCH = Toolkit.getDefaultToolkit().getScreenResolution();

    public static final int HORIZONTAL = 0;

    public static final int VERTICAL = 1;

    public static final int SIZE = 18;

    public int orientation;

    private boolean isMetric;

    private float unitSize;

    private int indicatorValue = -1;

    private float ticksPerUnit;

    private double zoomLevel = 1d;

    private double cmSpacing;

    private double inSpacing;

    private Rectangle2D selectionRect = null;

    public Ruler(int orientation, boolean isMetric) {
        this(orientation, isMetric, 0, 0);
    }

    public Ruler(int orientation, boolean isMetric, double cmSpacing, double inSpacing) {
        this.orientation = orientation;
        this.isMetric = isMetric;
        this.cmSpacing = cmSpacing;
        this.inSpacing = inSpacing;
        setIncrementAndUnits();
    }

    public void setSelectionRect(Rectangle2D selectionRect) {
        this.selectionRect = selectionRect;
        repaint();
    }

    public void setZoomLevel(double zoomLevel) {
        this.zoomLevel = zoomLevel;
        setIncrementAndUnits();
        repaint();
    }

    public void setIsMetric(boolean isMetric) {
        this.isMetric = isMetric;
        setIncrementAndUnits();
        repaint();
    }

    /**
     * Changes cursor position. If less than zero, indication will not be
     * rendered. For horizontal ruler this should be X coordinate of mouse
     * position, and Y for vertical.
     * 
     * @param indicatortValue
     */
    public void setIndicatorValue(int indicatortValue) {
        this.indicatorValue = indicatortValue;
    }

    private void setIncrementAndUnits() {
        if (isMetric) {
            unitSize = (float) ((cmSpacing == 0 ? PIXELS_PER_INCH / 2.54f : cmSpacing) * zoomLevel);
            ticksPerUnit = 4;
        } else {
            ticksPerUnit = 10;
            unitSize = (float) ((inSpacing == 0 ? (PIXELS_PER_INCH) : inSpacing) * zoomLevel);
        }
    }

    public boolean isMetric() {
        return this.isMetric;
    }

    public void setPreferredHeight(int ph) {
        setPreferredSize(new Dimension(SIZE, ph));
    }

    public void setPreferredWidth(int pw) {
        setPreferredSize(new Dimension(pw, SIZE));
    }

    protected void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;

        Rectangle clipRect = graphics.getClipBounds();

        graphics.setColor(COLOR);
        graphics.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);

        /*
         * Do the ruler labels in a small font that's black.
         */
        graphics.setFont(new Font(Platform.getPlatform().getDefaultFontName(), Font.PLAIN, 10));
        graphics.setColor(Color.black);

        /*
         * Some vars we need.
         */
        float start = 0;
        int tickLength = 0;
        String text = null;
        float increment = unitSize / ticksPerUnit;

        /*
         * Use clipping bounds to calculate first and last tick locations.
         */
        int firstUnit;
        if (orientation == HORIZONTAL) {
            firstUnit = (int) (clipRect.x / unitSize);
            start = (int) (clipRect.x / unitSize) * unitSize;
        } else {
            firstUnit = (int) (clipRect.y / unitSize);
            start = (int) (clipRect.y / unitSize) * unitSize;
        }

        /*
         * ticks and labels
         */
        int x = 0;
        int i = 0;

        while (x < (orientation == HORIZONTAL ? (clipRect.x + clipRect.width) : (clipRect.y + clipRect.height))) {
            if ((ticksPerUnit <= 1) || (i % Math.round(ticksPerUnit) == 0)) {
                tickLength = 10;
                text = Integer.toString(firstUnit + Math.round(i / ticksPerUnit));
            } else {
                tickLength = 7;
                if (isMetric) {
                    tickLength -= 2 * (i % Math.round(ticksPerUnit) % 2);
                } else if (i % Math.round(ticksPerUnit) != 5) {
                    tickLength -= 2;
                }
                text = null;
            }

            x = (int) (start + i * increment);

            if (tickLength != 0) {
                if (orientation == HORIZONTAL) {
                    graphics.drawLine(x, SIZE - 1, x, SIZE - tickLength - 1);
                    if (text != null) {
                        graphics.drawString(text, x + 2, 15);
                    }
                } else {
                    graphics.drawLine(SIZE - 1, x, SIZE - tickLength - 1, x);

                    if (text != null) {
                        FontMetrics fm = graphics.getFontMetrics();
                        graphics.drawString(text, SIZE - (int) fm.getStringBounds(text, graphics).getWidth() - 2, x + 10);
                    }
                }
            }
            i++;
        }

        /*
         * highlight value
         */
        if (indicatorValue >= 0) {
            graphics.setColor(Color.red);
            if (orientation == HORIZONTAL) {
                if (indicatorValue < getWidth()) {
                    graphics.drawLine(indicatorValue, 0, indicatorValue, SIZE - 1);
                }
            } else {
                if (indicatorValue < getHeight()) {
                    graphics.drawLine(0, indicatorValue, SIZE - 1, indicatorValue);
                }
            }
        }

        /*
         * selection
         */
        if (selectionRect != null) {
            graphics.setColor(SELECTION_COLOR);
            if (orientation == HORIZONTAL) {
                graphics.drawLine((int) selectionRect.getX(), 0, (int) selectionRect.getX(), SIZE - 1);
                graphics.drawLine((int) (selectionRect.getX() + selectionRect.getWidth()), 0,
                        (int) (selectionRect.getX() + selectionRect.getWidth()), SIZE - 1);
            } else {
                graphics.drawLine(0, (int) selectionRect.getY(), SIZE - 1, (int) selectionRect.getY());
                graphics.drawLine(0, (int) (selectionRect.getY() + selectionRect.getHeight()), SIZE - 1,
                        (int) (selectionRect.getY() + selectionRect.getHeight()));
            }
        }

        /*
         * lines
         */
        graphics.setColor(Color.black);
        if (orientation == HORIZONTAL) {
            graphics.drawLine(0, SIZE - 1, getWidth(), SIZE - 1);
        } else {
            graphics.drawLine(SIZE - 1, 0, SIZE - 1, getHeight());
        }
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }
}
