package org.diylc.app.view;

import java.lang.reflect.Method;

import org.diylc.components.IComponentFilter;
import org.diylc.components.PcbLayer;
import org.diylc.core.IDIYComponent;

public class PCBLayerFilter implements IComponentFilter {

  private PcbLayer layer;

  public PCBLayerFilter(PcbLayer layer) {
    super();
    this.layer = layer;
  }

  @Override
  public boolean testComponent(IDIYComponent component) {
    Class<?> clazz = component.getClass();

    try {
      Method m = clazz.getMethod("getLayer");
      PcbLayer l = (PcbLayer) m.invoke(component);

      return layer == l;
    } catch (Exception e) {
      return false;
    }
  }
}
