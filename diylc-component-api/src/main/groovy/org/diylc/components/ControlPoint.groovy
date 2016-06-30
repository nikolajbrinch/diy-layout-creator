package org.diylc.components

import java.awt.Point;
import java.util.Map;

class ControlPoint extends Point {

    Map<String, Object> properties = [:]

    ControlPoint() {
        super()
    }

    ControlPoint(ControlPoint controlPoint) {
        this(controlPoint.x, controlPoint.y, controlPoint.properties)
    }

    ControlPoint(Point point) {
        super(point)
    }

    ControlPoint(Point point, Map<String, Object> properties) {
        super(point)
        this.properties.putAll(properties)
    }

    ControlPoint(int x, int y) {
        super(x, y)
    }

    ControlPoint(int x, int y, Map<String, Object> properties) {
        super(x, y)
        this.properties.putAll(properties)
    }
}
