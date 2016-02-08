package org.diylc.app.controllers;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;
import org.diylc.core.EventType;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Template;
import org.diylc.core.events.EventListener;
import org.diylc.core.events.EventReciever;

public class CanvasController extends AbstractController implements ArrangeController,
        EditController, ClipboardOwner {

    private final EventReciever<EventType> eventReciever = new EventReciever<EventType>();

    private final Clipboard clipboard;

    public CanvasController(ApplicationController applicationController, View view, Model model, DrawingController controller, IPlugInPort plugInPort) {
        super(applicationController, view, model, controller, plugInPort);
        this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        eventReciever.registerListener(EnumSet.of(EventType.PROJECT_LOADED, EventType.ZOOM_CHANGED, EventType.REPAINT),
                new EventListener<EventType>() {

                    @Override
                    public void processEvent(EventType eventType, Object... params) {
                        switch (eventType) {
                        case PROJECT_LOADED:
                            getView().getCanvas().refreshSize();
                            if ((Boolean) params[1]) {
                                /* 
                                 * Scroll to the center.
                                 */
                                Rectangle visibleRect = getView().getCanvas().getVisibleRect();
                                visibleRect.setLocation((getView().getCanvas().getWidth() - visibleRect.width) / 2, (getView()
                                        .getCanvas().getHeight() - visibleRect.height) / 2);
                                getView().getCanvas().scrollRectToVisible(visibleRect);
                                getView().getCanvas().revalidate();
                            }
                            break;
                        case ZOOM_CHANGED:
                            Rectangle visibleRect = getView().getCanvas().getVisibleRect();
                            getView().getCanvas().refreshSize();
                            /* 
                             * Try to set the visible area to be centered with
                             * the previous
                             * one.
                             */
                            double zoomFactor = (Double) params[0] / getView().getZoomLevel();
                            visibleRect.setBounds((int) (visibleRect.x * zoomFactor), (int) (visibleRect.y * zoomFactor),
                                    visibleRect.width, visibleRect.height);
                            getView().getCanvas().scrollRectToVisible(visibleRect);
                            getView().getCanvas().revalidate();

                            getView().setZoomLevel((Double) params[0]);
                            break;
                        case REPAINT:
                            getView().getCanvas().repaint();
                            break;
                        default:
                            break;
                        }
                    }

                });
    }

    public void selectComponent(IDIYComponent component) {
        List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>();
        newSelection.add(component);
        getView().updateSelection(newSelection);
        getPlugInPort().refresh();
    }

    public void applyTemplate(Template template) {
        getPlugInPort().applyTemplateToSelection(template);        
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    public Clipboard getClipboard() {
        return clipboard;
    }

}
