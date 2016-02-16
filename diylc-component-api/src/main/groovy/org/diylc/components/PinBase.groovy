package org.diylc.components;

import java.awt.Rectangle
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.GeneralPath
import java.awt.geom.PathIterator
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;

public class PinBase implements Shape, Geometry {

    private GeneralPath path

    public static Size DEFAULT_BASE_SIZE = new Size(0.1d, SizeUnit.in)
    
    public static Size DEFAULT_CORNER_SIZE = new Size(DEFAULT_BASE_SIZE.value / 8, DEFAULT_BASE_SIZE.unit)
    
    public PinBase() {
        this(DEFAULT_BASE_SIZE.convertToPixels(), DEFAULT_CORNER_SIZE.convertToPixels())
    }

    public PinBase(int baseSize, int cornerSize) {
        this.path = createPath(baseSize, cornerSize)
    }

    @Override
    public Rectangle getBounds() {
        return path.getBounds();
    }

    @Override
    public Rectangle2D getBounds2D() {
        return path.getBounds2D();
    }

    @Override
    public boolean contains(double x, double y) {
        return path.contains(x, y);
    }

    @Override
    public boolean contains(Point2D p) {
        return path.contains(p);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return path.intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return path.intersects(r);
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return path.contains(x, y, w, h);
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return path.contains(r);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return path.getPathIterator(at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return path.getPathIterator(at, flatness);
    }

    private GeneralPath createPath(int headerSize, int cornerSize) {
        GeneralPath path = new GeneralPath()
        path.moveTo(toFloat(cornerSize), toFloat(0))

        /*
         * Horiz (x1 -> x2)
         */
        path.lineTo(toFloat(headerSize - cornerSize), toFloat(0))
        path.lineTo(toFloat(headerSize), toFloat(cornerSize))

        /*
         * Vert (y1 -> y2)
         */
        path.lineTo(toFloat(headerSize), toFloat(headerSize - cornerSize))
        path.lineTo(toFloat(headerSize - cornerSize), toFloat(headerSize))

        /*
         * Horiz (x2 -> x1)
         */
        path.lineTo(toFloat(cornerSize), toFloat(headerSize))
        path.lineTo(toFloat(0), toFloat(headerSize - cornerSize))

        /*
         * Vert (y2 -> y1)
         */
        path.lineTo(toFloat(0), toFloat(cornerSize))
        path.closePath()

        return path
    }
}