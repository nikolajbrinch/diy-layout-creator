package org.diylc.app.controllers;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;

import org.diylc.app.Layer;
import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;

public class LayersController extends AbstractController {

    /**
     * TODO: Move this to Presenter/Model
     */
    private final Map<Layer, Action> lockActionMap = new HashMap<Layer, Action>();

    /**
     * TODO: Move this to Presenter/Model
     */
    private final Map<Double, Action> selectAllActionMap = new HashMap<Double, Action>();

    public LayersController(ApplicationController applicationController, View view, Model model, IPlugInPort plugInPort) {
        super(applicationController, view, model, plugInPort);
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

    public void updateLockedLayers(Set<Double> lockedLayers) {
        for (Layer layer : Layer.values()) {
            lockActionMap.get(layer).putValue(Action.SELECTED_KEY, lockedLayers.contains(layer.getZOrder()));
        }
    }
}
