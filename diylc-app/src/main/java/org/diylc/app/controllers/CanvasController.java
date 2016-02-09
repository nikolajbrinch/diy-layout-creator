package org.diylc.app.controllers;

import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.util.ArrayList;
import java.util.List;

import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;
import org.diylc.app.view.canvas.Canvas;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.Template;

public class CanvasController extends AbstractController implements ArrangeController, EditController {

    public CanvasController(ApplicationController applicationController, View view, Model model, IPlugInPort plugInPort) {
        super(applicationController, view, model, plugInPort);
    }

    public void selectComponent(IDIYComponent component) {
        List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>();
        newSelection.add(component);
        getPlugInPort().updateSelection(newSelection);
        getPlugInPort().refresh();
    }

    public void applyTemplate(Template template) {
        getPlugInPort().applyTemplateToSelection(template);
    }

    @Override
    public Clipboard getClipboard() {
        return ((DrawingController) getClipboardOwner()).getClipboard();
    }

    @Override
    public ClipboardOwner getClipboardOwner() {
        return getApplicationController().getCurrentDrawing().getController();
    }
    
    public void updateZoomLevel(double zoomLevel) {
        Rectangle visibleRect = getCanvas().getVisibleRect();
        getCanvas().refreshSize();
        /*
         * Try to set the visible area to be centered with the previous one.
         */
        double zoomFactor = zoomLevel / getView().getZoomLevel();
        visibleRect.setBounds((int) (visibleRect.x * zoomFactor), (int) (visibleRect.y * zoomFactor), visibleRect.width,
                visibleRect.height);
        getCanvas().scrollRectToVisible(visibleRect);
        getCanvas().revalidate();

        getView().setZoomLevel(zoomLevel);
    }

    public void init(Project project, boolean freshStart) {
        getCanvas().refreshSize();
        if (freshStart) {
            /*
             * Scroll to the center.
             */
            Rectangle visibleRect = getCanvas().getVisibleRect();
            visibleRect.setLocation((getCanvas().getWidth() - visibleRect.width) / 2,
                    (getCanvas().getHeight() - visibleRect.height) / 2);
            getCanvas().scrollRectToVisible(visibleRect);
            getCanvas().revalidate();
        }
    }
    
    private Canvas getCanvas() {
        return getView().getCanvas();
    }

}
