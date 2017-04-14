package org.diylc.app.controllers;

import org.diylc.app.Drawing;
import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;

import lombok.Getter;

@Getter
public abstract class AbstractController implements MenuController {

  private final IPlugInPort plugInPort;

  private final View view;

  private final Model model;

  private final ApplicationController applicationController;

  public AbstractController(ApplicationController applicationController, View view, Model model,
      IPlugInPort plugInPort) {
    this.applicationController = applicationController;
    this.view = view;
    this.model = model;
    this.plugInPort = plugInPort;
  }

  public Drawing getDrawing() {
    return getView().getDrawing();
  }
}
