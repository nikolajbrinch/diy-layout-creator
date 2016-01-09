package org.diylc.components

import java.awt.Point
import java.awt.Rectangle;

trait Geometry {

    Point point(def x, def y) {
        return new Point((int) x, (int) y)
    }

    Point[] points(Point... points) {
        return points as Point[]
    }

    Rectangle rectangle(def x, def y, def width, def height) {
        return new Rectangle((int)x, (int)y, (int)width, (int)height)
    }
}
