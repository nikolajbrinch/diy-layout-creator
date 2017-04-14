package org.diylc.app.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.EnumSet;

import org.diylc.app.view.canvas.IDrawingProvider;
import org.diylc.app.view.rendering.DrawingOption;
import org.diylc.core.config.Configuration;

/**
 * {@link IDrawingProvider} implementation that uses {@link IPlugInPort} to draw a project onto the
 * canvas.
 * 
 * @author Branislav Stojkovic
 */
public class ProjectDrawingProvider implements IDrawingProvider {

  private IPlugInPort plugInPort;

  private boolean useZoom;

  private boolean showGridWhenNeeded;

  public ProjectDrawingProvider(IPlugInPort plugInPort, boolean useZoom,
      boolean showGridWhenNeeded) {
    super();
    this.plugInPort = plugInPort;
    this.useZoom = useZoom;
    this.showGridWhenNeeded = showGridWhenNeeded;
  }

  @Override
  public Dimension getSize() {
    return plugInPort.getCanvasDimensions(useZoom);
  }

  @Override
  public void draw(int page, Graphics g) {
    EnumSet<DrawingOption> drawOptions = EnumSet.of(DrawingOption.ANTIALIASING);
    if (useZoom) {
      drawOptions.add(DrawingOption.ZOOM);
    }
    if (showGridWhenNeeded && Configuration.INSTANCE.getExportGrid()) {
      drawOptions.add(DrawingOption.GRID);
    }
    if (Configuration.INSTANCE.getOutline()) {
      drawOptions.add(DrawingOption.OUTLINE_MODE);
    }
    plugInPort.draw((Graphics2D) g, drawOptions, null);
  }

  @Override
  public int getPageCount() {
    return 1;
  }
}
