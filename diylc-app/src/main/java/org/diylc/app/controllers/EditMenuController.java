package org.diylc.app.controllers;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;

import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;

public class EditMenuController extends AbstractController implements EditController {

  public EditMenuController(ApplicationController applicationController, View view, Model model,
      IPlugInPort plugInPort) {
    super(applicationController, view, model, plugInPort);
  }

  public void selectAll() {
    LOG.info("Select All triggered");
    getPlugInPort().selectAll(0);
  }

  public void renumber(boolean xAxisFirst) {
    LOG.info("Renumber action triggered X-Axis first: " + xAxisFirst);
    getPlugInPort().renumberSelectedComponents(xAxisFirst);
  }

  @Override
  public Clipboard getClipboard() {
    return ((DrawingController) getClipboardOwner()).getClipboard();
  }

  @Override
  public ClipboardOwner getClipboardOwner() {
    return getApplicationController().getCurrentDrawing().getController();
  }

}
