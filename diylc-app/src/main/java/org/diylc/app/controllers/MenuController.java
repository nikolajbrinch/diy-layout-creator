package org.diylc.app.controllers;

import org.diylc.app.Drawing;
import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;

public interface MenuController extends Controller {

  public IPlugInPort getPlugInPort();

  public Model getModel();

  public ApplicationController getApplicationController();

  public Drawing getDrawing();

}
