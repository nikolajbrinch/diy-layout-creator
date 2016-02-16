package org.diylc.app.controllers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;

import org.diylc.app.model.Model;
import org.diylc.app.view.DrawingView;
import org.diylc.core.Project;

public class DrawingController implements ClipboardOwner {

    private final ApplicationController applicationController;

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

    public ApplicationController getApplicationController() {
        return applicationController;
    }

    public DrawingView getView() {
        return view;
    }

    public Clipboard getClipboard() {
        return clipboard;
    }

    public void dispose() {
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        getView().refreshActions();
    }

    public void autoSave(Project project) {
        getApplicationController().autoSave(project);
    }

    public void setView(DrawingView view) {
        this.view = view;
    }

    public Model getModel() {
        return model;
    }

}
