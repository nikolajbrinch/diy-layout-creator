package org.diylc.components

import java.awt.Point
import java.awt.Rectangle;

trait Geometry {

    Point point(def point) {
        return new Point(point as Point)
    }

    Point point(def x, def y) {
        return new Point(x as int, y as int)
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
