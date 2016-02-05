package org.diylc.app.controllers;

import org.diylc.app.model.DrawingModel;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;

public class WindowController extends AbstractController {

    public WindowController(ApplicationController applicationController, View view, DrawingModel model, IPlugInPort plugInPort) {
        super(applicationController, view, model, plugInPort);
    }

    public void minimize() {
    }

    public void zoom() {
    }

    public void bringAllToFront() {
    }

}
