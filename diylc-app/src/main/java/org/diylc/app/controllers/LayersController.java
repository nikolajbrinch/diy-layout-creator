package org.diylc.app.controllers;

import java.awt.event.ActionEvent;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;

import org.diylc.app.Layer;
import org.diylc.app.model.DrawingModel;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;
import org.diylc.core.EventType;
import org.diylc.core.events.EventListener;
import org.diylc.core.events.EventReciever;

public class LayersController extends AbstractController {

    private final EventReciever<EventType> eventReciever = new EventReciever<EventType>();

    private final Map<Layer, Action> lockActionMap = new HashMap<Layer, Action>();
    
    private final Map<Double, Action> selectAllActionMap = new HashMap<Double, Action>();

    public LayersController(ApplicationController applicationController, View view, DrawingModel model, IPlugInPort plugInPort) {
        super(applicationController, view, model, plugInPort);
        
        eventReciever.registerListener(EnumSet.of(EventType.LAYER_STATE_CHANGED), new EventListener<EventType>() {

            @SuppressWarnings("unchecked")
            @Override
            public void processEvent(EventType eventType, Object... params) {
                if (eventType == EventType.LAYER_STATE_CHANGED) {
                    Set<Integer> lockedLayers = (Set<Integer>) params[0];
                    for (Layer layer : Layer.values()) {
                        lockActionMap.get(layer).putValue(Action.SELECTED_KEY,
                                lockedLayers.contains(layer.getZOrder()));
                    }
                }
            }

        });
    }

    public void lock(ActionEvent event, double zOrder) {
        JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
        getPlugInPort().setLayerLocked(zOrder, menuItem.isSelected());
        selectAllActionMap.get(zOrder).setEnabled(!(Boolean) menuItem.isSelected());
    }
    
    public void selectAll(double zOrder) {
        getPlugInPort().selectAll(zOrder);
    }

    public void addLockAction(Layer layer, Action lockAction) {
        lockActionMap.put(layer, lockAction);
    }

    public void addSelectAllAction(double zOrder, Action selectAllAction) {
        selectAllActionMap.put(zOrder, selectAllAction);
    }
}
