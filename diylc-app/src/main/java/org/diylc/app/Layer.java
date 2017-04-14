package org.diylc.app;

import org.diylc.core.IDIYComponent;

import lombok.Getter;

public enum Layer {
  CHASSIS("Chassis", IDIYComponent.CHASSIS), BOARD("Board",
      IDIYComponent.BOARD),
  TRACE("Trace", IDIYComponent.TRACE), COMPONENT(
      "Component", IDIYComponent.COMPONENT),
  TEXT("Text",
      IDIYComponent.TEXT);

  @Getter
  private String title;

  @Getter
  private double zOrder;

  private Layer(String title, double zOrder) {
    this.title = title;
    this.zOrder = zOrder;
  }

}
