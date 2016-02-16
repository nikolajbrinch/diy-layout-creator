package org.diylc.components

import java.awt.Point;
import java.awt.Rectangle;

trait Geometry {

    ControlPoint point(def point) {
        return new ControlPoint(point as Point)
    }

    ControlPoint point(def point, Map<String, Object> properties) {
        return new ControlPoint(point as Point, properties)
    }

    ControlPoint point(def x, def y) {
        return new ControlPoint(x as int, y as int)
    }

    ControlPoint point(def x, def y, Map<String, Object> properties) {
        return new ControlPoint(x as int, y as int, properties)
    }

    Point[] points(Point... points) {
        return points as Point[]
    }

    Rectangle rectangle(def x, def y, def width, def height) {
        return new Rectangle(x as int, y as int, width as int, height as int)
    }

    float toFloat(def number) {
        return number as float
    }

    int toInt(def number) {
        return number as int
    }
}
