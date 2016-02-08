package org.diylc.app;

import java.nio.file.Path;

import org.diylc.app.controllers.ApplicationController;
import org.diylc.app.controllers.DrawingController;
import org.diylc.app.model.DrawingModel;
import org.diylc.app.view.DrawingView;

public class Drawing {

    private DrawingView view;

    private DrawingModel model;

    private DrawingController controller;

    public Drawing(ApplicationController applicationController) {
        this.model = new DrawingModel();
        this.view = new DrawingView(applicationController, this, model);
        this.controller = new DrawingController(applicationController, view, model);
        getView().setController(getController());
        getModel().setView(getView());
        getView().setVisible(true);
    }

    public Drawing(ApplicationController applicationController, Path path) {
        this(applicationController);

        try {
            getView().getModel().loadProject(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DrawingView getView() {
        return view;
    }

    public void dispose() {
        getView().block();
        getModel().dispose();
        getView().setVisible(false);
        getView().dispose();
    }

    public DrawingModel getModel() {
        return model;
    }

    public boolean allowFileAction() {
        return getModel().allowFileAction();
    }

    public DrawingController getController() {
        return controller;
    }
}
