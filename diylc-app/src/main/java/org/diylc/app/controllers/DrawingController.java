package org.diylc.app.controllers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;

import org.diylc.app.Drawing;
import org.diylc.app.view.DrawingView;

public class DrawingController {

    private final ApplicationController applicationController;

    private final DrawingView view;

    private final Clipboard clipboard;

    private boolean closed = false;

    public DrawingController(ApplicationController applicationController, DrawingView view) {
        this.applicationController = applicationController;
        this.view = view;

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

    public synchronized boolean close() {
        if (!closed) {
            if (getDrawing().allowFileAction()) {
                getDrawing().dispose();
                closed = true;
            }
        }

        return closed;
    }

    private Drawing getDrawing() {
        return getView().getDrawing();
    }

}
