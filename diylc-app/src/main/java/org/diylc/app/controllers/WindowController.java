package org.diylc.app.controllers;

import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;

public class WindowController extends AbstractController {

    public WindowController(ApplicationController applicationController, View view, Model model, DrawingController controller, IPlugInPort plugInPort) {
        super(applicationController, view, model, controller, plugInPort);
    }

    public void minimize() {
    }

    public void zoom() {
    }

    public void bringAllToFront() {
    }

}
