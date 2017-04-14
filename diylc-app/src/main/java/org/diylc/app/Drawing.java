package org.diylc.app;

import java.nio.file.Path;
import java.util.UUID;

import org.diylc.app.controllers.ApplicationController;
import org.diylc.app.controllers.DrawingController;
import org.diylc.app.model.DrawingModel;
import org.diylc.app.view.DrawingView;
import org.diylc.app.view.Presenter;
import org.diylc.core.Project;

import lombok.Getter;

public class Drawing {

  @Getter
  private final DrawingView view;

  @Getter
  private final DrawingModel model;

  @Getter
  private final DrawingController controller;

  @Getter
  private final String id;

  private final Presenter presenter;

  private boolean closed = false;

  public Drawing(ApplicationController applicationController, Project project, Path path,
      boolean load) {
    this.id = UUID.randomUUID().toString();
    this.model = new DrawingModel();
    this.controller = new DrawingController(applicationController, model);
    this.view = new DrawingView(applicationController, this, controller, path, load);
    this.presenter = getView().getPresenter();
    getModel().setPresenter(presenter);

    getView().setVisible(true);

    if (load) {
      try {
        getModel().loadProject(project, false);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public synchronized boolean close() {
    if (!closed) {
      getView().block();
      if (allowFileAction()) {
        getController().dispose();
        getModel().dispose();
        getView().setVisible(false);
        getView().dispose();
        closed = true;
      } else {
        getView().unblock();
      }
    }
    return closed;
  }

  public boolean allowFileAction() {
    return presenter.allowFileAction();
  }

  public String getTitle() {
    return getView().getTitle();
  }

}
