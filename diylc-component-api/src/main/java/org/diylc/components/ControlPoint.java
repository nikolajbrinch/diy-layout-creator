package org.diylc.components;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class ControlPoint extends Point {

  /**
   * 
   */
  private static final long serialVersionUID = 6718529252547901643L;

  @Getter
  @Setter
  Map<String, Object> properties = new HashMap<>();

  ControlPoint() {
    super();
  }

  ControlPoint(Point point) {
    super(point);
  }

  ControlPoint(Point point, Map<String, Object> properties) {
    super(point);
    this.properties = properties;
  }

  ControlPoint(int x, int y) {
    super(x, y);
  }

  ControlPoint(int x, int y, Map<String, Object> properties) {
    super(x, y);
    this.properties = properties;
  }
}
