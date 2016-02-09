package org.diylc.app.controllers;

import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;

public class ArrangeMenuController extends AbstractController implements ArrangeController {

    public ArrangeMenuController(ApplicationController applicationController, View view, Model model, IPlugInPort plugInPort) {
        super(applicationController, view, model, plugInPort);
    }

}
