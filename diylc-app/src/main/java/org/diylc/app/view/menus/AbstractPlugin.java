package org.diylc.app.view.menus;

import org.diylc.app.controllers.Controller;
import org.diylc.app.model.DrawingModel;
import org.diylc.app.view.IPlugIn;
import org.diylc.app.view.View;

public abstract class AbstractPlugin<T extends Controller> implements IPlugIn {

    private final T controller;
    
    private final DrawingModel model;

    private final View view;
    
    public AbstractPlugin(T controller, View view, DrawingModel model) {
        this.controller = controller;
        this.model = model;
        this.view = view;
    }

    public T getController() {
        return controller;
    }

    public DrawingModel getModel() {
        return model;
    }

    public View getView() {
        return view;
    }

}
