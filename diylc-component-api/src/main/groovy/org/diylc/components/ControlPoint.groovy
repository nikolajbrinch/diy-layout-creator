package org.diylc.components

import java.awt.Point;
import java.util.Map;

class ControlPoint extends Point {

    Map<String, Object> properties = [:]

    ControlPoint() {
        super()
    }

    ControlPoint(Point point) {
        super(point)
    }

    ControlPoint(Point point, Map<String, Object> properties) {
        super(point)
        this.properties = properties
    }

    ControlPoint(int x, int y) {
        super(x, y)
        this.properties = properties
    }

    ControlPoint(int x, int y, Map<String, Object> properties) {
        super(x, y)
        this.properties = properties
    }
}
