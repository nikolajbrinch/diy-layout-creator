package org.diylc.app.view.menus;

import org.diylc.app.controllers.Controller;
import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugIn;
import org.diylc.app.view.View;

public abstract class AbstractPlugin<T extends Controller> implements IPlugIn {

    private final T controller;
    
    private final Model model;

    private final View view;
    
    public AbstractPlugin(T controller, View view, Model model) {
        this.controller = controller;
        this.model = model;
        this.view = view;
    }

    public T getController() {
        return controller;
    }

    public Model getModel() {
        return model;
    }

    public View getView() {
        return view;
    }

}
