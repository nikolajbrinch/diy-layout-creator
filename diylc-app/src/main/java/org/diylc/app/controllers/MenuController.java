package org.diylc.app.controllers;

import org.diylc.app.Drawing;
import org.diylc.app.model.DrawingModel;
import org.diylc.app.view.IPlugInPort;

public interface MenuController extends Controller {

    public IPlugInPort getPlugInPort();
    
    public DrawingModel getModel();
    
    public ApplicationController getApplicationController();
    
    public Drawing getDrawing();

}
