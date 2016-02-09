package org.diylc.app.controllers;

import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;

public class WindowController extends AbstractController {

    public WindowController(ApplicationController applicationController, View view, Model model, IPlugInPort plugInPort) {
        super(applicationController, view, model, plugInPort);
    }

    public void minimize() {
    }

    public void zoom() {
    }

    public void bringAllToFront() {
    }

}
