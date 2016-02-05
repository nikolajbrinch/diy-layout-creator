package org.diylc.app;

import java.nio.file.Path;

import org.diylc.app.controllers.ApplicationController;
import org.diylc.app.controllers.DrawingController;
import org.diylc.app.model.DrawingModel;
import org.diylc.app.view.DrawingView;

public class Drawing {

    private DrawingView view;

    public Drawing(ApplicationController applicationController) {
        this.view = new DrawingView(applicationController, this);
        getView().setController(new DrawingController(applicationController, getView()));
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
        return getView().getModel();
    }

    public boolean allowFileAction() {
        return getModel().allowFileAction();
    }

    public DrawingController getController() {
        return getView().getController();
    }
}
