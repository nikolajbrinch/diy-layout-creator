package org.diylc.app.controllers;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;

import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;
import org.diylc.core.config.Configuration;

public class ConfigController extends AbstractController {

    public ConfigController(ApplicationController applicationController, View view, Model model, IPlugInPort plugInPort) {
        super(applicationController, view, model, plugInPort);
    }

    public void autoCreatePads(ActionEvent event) {
        JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
        Configuration.INSTANCE.setProperty(Configuration.Key.AUTO_CREATE_PADS, menuItem.isSelected());
        getPlugInPort().refresh();
    }

    public void autoEditMode(ActionEvent event) {
        JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
        Configuration.INSTANCE.setProperty(Configuration.Key.AUTO_EDIT, menuItem.isSelected());
        getPlugInPort().refresh();
    }

    public void continuousCreation(ActionEvent event) {
        JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
        Configuration.INSTANCE.setProperty(Configuration.Key.CONTINUOUS_CREATION, menuItem.isSelected());
        getPlugInPort().refresh();
    }

    public void exportGrid(ActionEvent event) {
        JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
        Configuration.INSTANCE.setProperty(Configuration.Key.EXPORT_GRID, menuItem.isSelected());
        getPlugInPort().refresh();
    }

    public void snapToGrip(ActionEvent event) {
        JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
        Configuration.INSTANCE.setProperty(Configuration.Key.SNAP_TO_GRID, menuItem.isSelected());
        getPlugInPort().refresh();
    }

    public void stickyPoints(ActionEvent event) {
        JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
        Configuration.INSTANCE.setProperty(Configuration.Key.STICKY_POINTS, menuItem.isSelected());
        getPlugInPort().refresh();
    }

}
