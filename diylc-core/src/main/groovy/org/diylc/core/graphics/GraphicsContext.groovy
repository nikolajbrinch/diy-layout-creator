package org.diylc.core.graphics

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;

class GraphicsContext {

    public Graphics2D graphics2D

    public GraphicsContext(Graphics2D graphics2D) {
        this.graphics2D = graphics2D
    }

    public FontMetrics getFontMetrics() {
        return graphics2D.getFontMetrics()
    }

    public FontMetrics getFontMetrics(Font font) {
        return graphics2D.getFontMetrics(font)
    }

    public Font getFont() {
        return graphics2D.getFont()
    }

    public void setFont(Font font) {
        graphics2D.setFont(font)
    }

    public FontRenderContext getFontRenderContext() {
        return graphics2D.getFontRenderContext()
    }

    public void setStroke(Stroke stroke) {
        graphics2D.setStroke(stroke)
    }

    public Composite getComposite() {
        return graphics2D.getComposite()
    }

    public void setComposite(Composite composite) {
        graphics2D.setComposite(composite)
    }

    public void setColor(Color color) {
        graphics2D.setColor(color)
    }

    public Shape getClip() {
        graphics2D.getClip()
    }

    public void drawString(String text, def x, def y) {
        graphics2D.drawString(text, (int) x, (int) y)
    }

    public void fillRect(def x, def y, def width, def height) {
        graphics2D.fillRect((int) x, (int) y, (int) width, (int) height)
    }

    public void fillRect(def rect) {
        graphics2D.fillRect((int) (rect as Rectangle).x, (int) (rect as Rectangle).y, (int) (rect as Rectangle).width, (int) (rect as Rectangle).height)
    }

    public void drawRect(def x, def y, def width, def height) {
        graphics2D.drawRect((int) x, (int) y, (int) width, (int) height)
    }

    public void drawRect(def rect) {
        graphics2D.drawRect((int) (rect as Rectangle).x, (int) (rect as Rectangle).y, (int) (rect as Rectangle).width, (int) (rect as Rectangle).height)
    }

    public void fillRoundRect(def x, def y, def width, def height, def arcWidth, def arcHeight) {
        graphics2D.fillRoundRect((int) x, (int) y, (int) width, (int) height, (int) arcWidth, (int) arcHeight)
    }

    public void drawRoundRect(def x, def y, def width, def height, def arcWidth, def arcHeight) {
        graphics2D.drawRoundRect((int) x, (int) y, (int) width, (int) height, (int) arcWidth, (int) arcHeight)
    }

    public void drawLine(def x1, def y1, def x2, def y2) {
        graphics2D.drawLine((int) x1, (int) y1, (int) x2, (int) y2)
    }

    public void fillOval(def x, def y, def width, def height) {
        graphics2D.fillOval((int) x, (int) y, (int) width, (int) height)
    }

    public void drawOval(def x, def y, def width, def height) {
        graphics2D.drawOval((int) x, (int) y, (int) width, (int) height)
    }

    public void drawFilledRect(def topLeft, def size, Color border, Color fill) {
        drawFilledRect(topLeft, topLeft, size, border, fill)
    }

    public void drawFilledRect(def x, def y, def size, Color border, Color fill) {
        drawFilledRect(x, y, size, size, border, fill)
    }

    public void drawFilledRect(def x, def y, def width, def height, Color border, Color fill) {
        setColor(fill)
        fillRect(x, y, width, height)
        setColor(border)
        drawRect(x, y, width, height)
    }

    public void drawFilledOval(def topLeft, def size, Color border, Color fill) {
        drawFilledOval(topLeft, topLeft, size, border, fill);
    }

    public void drawFilledOval(def x, def y, def size, Color border, Color fill) {
        drawFilledOval(x, y, size, size, border, fill);
    }

    public void drawFilledOval(def x, def y, def width, def height, Color border, Color fill) {
        setColor(fill)
        fillOval(x, y, width, height)
        setColor(border)
        drawOval(x, y, width, height)
    }

    public void draw(Shape shape) {
        graphics2D.draw(shape)
    }

    public void fill(Shape shape) {
        graphics2D.fill(shape)
    }

    public AffineTransform getTransform() {
        graphics2D.getTransform()
    }

    public void setTransform(AffineTransform transform) {
        graphics2D.setTransform(transform)
    }

    public void transform(AffineTransform transform) {
        graphics2D.transform(transform)
    }

    public void translate(def x, def y) {
        graphics2D.translate((double) x, (double) y)
    }

    public void rotate(def theta, def x, def y) {
        graphics2D.rotate((double) theta, (double) x, (double) y)
    }

    public void fillPolygon(def xPoints, def yPoints, def nPoints) {
        graphics2D.fillPolygon((int[]) xPoints, (int[]) yPoints, (int) nPoints)
    }

    public void fillPolygon(Polygon polygon) {
        graphics2D.fillPolygon(polygon)
    }

    public void drawPolygon(def xPoints, def yPoints, def nPoints) {
        graphics2D.drawPolygon((int[]) xPoints, (int[]) yPoints, (int) nPoints)
    }

    public void drawPolyline(def xPoints, def yPoints, def nPoints) {
        graphics2D.drawPolyline((int[]) xPoints, (int[]) yPoints, (int) nPoints)
    }

    public void scale(def sx, def sy) {
        graphics2D.scale((double) sx, (double) sy)
    }

    public void drawImage(Image image, def x, def y, ImageObserver imageObserver) {
        graphics2D.drawImage(image, (int) x, (int) y, imageObserver)
    }

    public void setClip(def x, def y, def width, def height) {
        graphics2D.setClip((int) x, (int) y, (int) width, (int) height)
    }

}
