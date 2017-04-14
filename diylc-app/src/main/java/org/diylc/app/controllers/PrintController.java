package org.diylc.app.controllers;


import java.awt.print.PrinterException;

import org.diylc.app.view.DrawingExporter;
import org.diylc.app.view.canvas.IDrawingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface PrintController extends MenuController {

  static final Logger LOG = LoggerFactory.getLogger(PrintController.class);

  public IDrawingProvider getDrawingProvider();

  default void print() {
    LOG.info("PrintAction triggered");
    try {
      DrawingExporter.getInstance().print(getDrawingProvider());
    } catch (PrinterException e1) {
      LOG.warn("Error printing", e1);
    }
  }

}
