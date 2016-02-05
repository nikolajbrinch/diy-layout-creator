package org.diylc.app.controllers;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.util.EnumSet;

import org.diylc.app.IUndoListener;
import org.diylc.app.UndoHandler;
import org.diylc.app.model.DrawingModel;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;
import org.diylc.core.EventType;
import org.diylc.core.Project;
import org.diylc.core.events.EventListener;
import org.diylc.core.events.EventReciever;

public class EditMenuController extends AbstractController implements EditController {

    private final EventReciever<EventType> eventReciever = new EventReciever<EventType>();

    private final UndoHandler<Project> undoHandler;

    public EditMenuController(ApplicationController applicationController, View view, DrawingModel model, IPlugInPort plugInPort) {
        super(applicationController, view, model, plugInPort);

        this.undoHandler = new UndoHandler<Project>(new IUndoListener<Project>() {

            @Override
            public void actionPerformed(Project currentState) {
                loadProject(currentState);
            }
        });
        
        eventReciever.registerListener(EnumSet.of(EventType.SELECTION_CHANGED, EventType.PROJECT_MODIFIED), new EventListener<EventType>() {

            @Override
            public void processEvent(EventType eventType, Object... params) {
                switch (eventType) {
                case SELECTION_CHANGED:
                    getView().refreshActions();
                    break;
                case PROJECT_MODIFIED:
                    getUndoHandler().stateChanged((Project) params[0], (Project) params[1], (String) params[2]);
                    break;
                default:
                    break;
                }
            }

        });
    }
    
    public void selectAll() {
        LOG.info("Select All triggered");
        getPlugInPort().selectAll(0);
    }

    public void renumber(boolean xAxisFirst) {
        LOG.info("Renumber action triggered X-Axis first: " + xAxisFirst);
        getPlugInPort().renumberSelectedComponents(xAxisFirst);        
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        getView().refreshActions();
    }

    @Override
    public Clipboard getClipboard() {
        return getApplicationController().getCurrentDrawing().getController().getClipboard();
    }

    public void loadProject(Project project) {
        getPlugInPort().loadProject(project, false);
    }

    public UndoHandler<Project> getUndoHandler() {
        return undoHandler;
    }

}
