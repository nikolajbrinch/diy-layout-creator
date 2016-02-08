package org.diylc.app.controllers;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.EnumSet;

import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;
import org.diylc.core.EventType;
import org.diylc.core.events.EventListener;
import org.diylc.core.events.EventReciever;

public class ArrangeMenuController extends AbstractController implements ArrangeController, ClipboardOwner {

    private final EventReciever<EventType> eventReciever = new EventReciever<EventType>();

    public ArrangeMenuController(ApplicationController applicationController, View view, Model model, DrawingController controller, IPlugInPort plugInPort) {
        super(applicationController, view, model, controller, plugInPort);

        eventReciever.registerListener(EnumSet.of(EventType.SELECTION_CHANGED), new EventListener<EventType>() {

            @Override
            public void processEvent(EventType eventType, Object... params) {
                if (eventType == EventType.SELECTION_CHANGED) {
                    getView().refreshActions();
                }
            }

        });
    }
    
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        getView().refreshActions();
    }

}
