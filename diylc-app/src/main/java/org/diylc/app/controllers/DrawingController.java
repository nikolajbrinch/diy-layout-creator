package org.diylc.app.controllers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;

import org.diylc.app.model.Model;
import org.diylc.app.view.DrawingView;
import org.diylc.components.registry.ComponentRegistry;
import org.diylc.core.Project;

import lombok.Getter;
import lombok.Setter;

@Getter
public class DrawingController implements ClipboardOwner {

  private final ApplicationController applicationController;

  @Setter
  private DrawingView view;

  private final Clipboard clipboard;

  private final Model model;

  public DrawingController(ApplicationController applicationController, Model model) {
    this.applicationController = applicationController;
    this.model = model;

    this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    this.clipboard.addFlavorListener(new FlavorListener() {

      @Override
      public void flavorsChanged(FlavorEvent e) {
        getView().refreshActions();
      }
    });
  }

  public void dispose() {}

  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    getView().refreshActions();
  }

  public void autoSave(Project project) {
    getApplicationController().autoSave(project);
  }

  public ComponentRegistry getComponentRegistry() {
    return getApplicationController().getComponentRegistry();
  }

}
