package org.diylc.app.view.canvas;

import java.awt.Rectangle;

public interface Canvas {

    public void refreshSize();

    public Rectangle getVisibleRect();

    public int getWidth();

    public int getHeight();

    public void scrollRectToVisible(Rectangle visibleRect);

    public void revalidate();

    public void repaint();

    public double getZoomLevel();

    public void setZoomLevel(double zoomLevel);

}
