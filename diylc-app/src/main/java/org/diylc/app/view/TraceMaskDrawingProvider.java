package org.diylc.app.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.diylc.app.view.canvas.IDrawingProvider;
import org.diylc.app.view.rendering.DrawingOption;
import org.diylc.components.PcbLayer;
import org.diylc.core.IDIYComponent;

/**
 * {@link IDrawingProvider} implementation that uses {@link IPlugInPort} to draw a project onto the
 * canvas.
 * 
 * @author Branislav Stojkovic
 */
public class TraceMaskDrawingProvider implements IDrawingProvider {

  private final IPlugInPort plugInPort;

  public TraceMaskDrawingProvider(IPlugInPort plugInPort) {
    super();
    this.plugInPort = plugInPort;
  }

  @Override
  public Dimension getSize() {
    return plugInPort.getCanvasDimensions(false);
  }

  @Override
  public void draw(int page, Graphics g) {
    plugInPort.draw((Graphics2D) g, EnumSet.of(DrawingOption.ANTIALIASING),
        new PCBLayerFilter(getUsedLayers()[page]));
  }

  @Override
  public int getPageCount() {
    return getUsedLayers().length;
  }

  private PcbLayer[] getUsedLayers() {
    Set<PcbLayer> layers = EnumSet.noneOf(PcbLayer.class);
    for (IDIYComponent c : plugInPort.getCurrentProject()
        .getComponents()) {
      Class<?> clazz = c.getClass();
      try {
        Method m = clazz.getMethod("getLayer");
        PcbLayer l = (PcbLayer) m.invoke(c);
        layers.add(l);
      } catch (Exception e) {
      }
    }
    List<PcbLayer> sorted = new ArrayList<PcbLayer>(layers);
    Collections.sort(sorted);
    return sorted.toArray(new PcbLayer[] {});
  }
}
