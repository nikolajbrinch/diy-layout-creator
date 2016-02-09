package org.diylc.app;

import java.nio.file.Path;
import java.util.UUID;

import org.diylc.app.controllers.ApplicationController;
import org.diylc.app.controllers.DrawingController;
import org.diylc.app.model.DrawingModel;
import org.diylc.app.view.DrawingView;
import org.diylc.app.view.Presenter;

public class Drawing {

    private final DrawingView view;

    private final DrawingModel model;

    private final DrawingController controller;

    private final Presenter presenter;

    private final String id;


    public Drawing(ApplicationController applicationController, Path path, boolean load) {
        this.id = UUID.randomUUID().toString();
        this.model = new DrawingModel();
        this.controller = new DrawingController(applicationController, model);
        this.view = new DrawingView(applicationController, this, controller, path, load);
        this.presenter = getView().getPresenter();
        getModel().setPresenter(presenter);

        getView().setVisible(true);

        if (load) {
            try {
                getModel().loadProject(path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
        return presenter.allowFileAction();
    }

    public DrawingController getController() {
        return controller;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return getView().getTitle();
    }
}
