package org.diylc.app.controllers;

import java.util.List;

import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;
import org.diylc.app.view.bom.BomDialog;
import org.diylc.app.view.canvas.IDrawingProvider;
import org.diylc.app.view.dialogs.DialogFactory;
import org.diylc.core.BomEntry;
import org.diylc.core.BomMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToolsController extends AbstractController
    implements ExportController, PrintController {

  static final Logger LOG = LoggerFactory.getLogger(ToolsController.class);

  private final IDrawingProvider drawingProvider;

  public ToolsController(ApplicationController applicationController, View view, Model model,
      IPlugInPort plugInPort, IDrawingProvider drawingProvider) {
    super(applicationController, view, model, plugInPort);
    this.drawingProvider = drawingProvider;
  }

  @Override
  public IDrawingProvider getDrawingProvider() {
    return drawingProvider;
  }

  public void createBom() {
    LOG.info("CreateBomAction triggered");
    List<BomEntry> bom =
        BomMaker.getInstance().createBom(getPlugInPort().getCurrentProject().getComponents());
    BomDialog dialog = DialogFactory.getInstance().createBomDialog(bom);
    dialog.setVisible(true);
  }

}
