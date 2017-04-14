package org.diylc.app.controllers;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;

import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;
import org.diylc.core.Theme;
import org.diylc.core.config.Configuration;

public class ViewController extends AbstractController {

  public ViewController(ApplicationController applicationController, View view, Model model,
      IPlugInPort plugInPort) {
    super(applicationController, view, model, plugInPort);
  }

  public void antiAliasing(ActionEvent event) {
    JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
    Configuration.INSTANCE.setProperty(Configuration.Key.ANTI_ALIASING, menuItem.isSelected());
    getPlugInPort().refresh();
  }

  public void hiQualityRendering(ActionEvent event) {
    JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
    Configuration.INSTANCE.setProperty(Configuration.Key.HI_QUALITY_RENDER, menuItem.isSelected());
    getPlugInPort().refresh();
  }

  public void outlineMode(ActionEvent event) {
    JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
    Configuration.INSTANCE.setProperty(Configuration.Key.OUTLINE, menuItem.isSelected());
    getPlugInPort().refresh();
  }

  public void mouseWheelZoom(ActionEvent event) {
    JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
    Configuration.INSTANCE.setProperty(Configuration.Key.WHEEL_ZOOM, menuItem.isSelected());
    getPlugInPort().refresh();
  }

  public void propertyPanel(ActionEvent event) {
    JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
    Configuration.INSTANCE.setProperty(Configuration.Key.PROPERTY_PANEL, menuItem.isSelected());
    getPlugInPort().refresh();
  }

  public void switchTheme(Theme theme) {
    getPlugInPort().setSelectedTheme(theme);
  }

}
