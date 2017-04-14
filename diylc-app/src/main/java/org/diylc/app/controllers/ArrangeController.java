package org.diylc.app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ArrangeController extends MenuController {

  static final Logger LOG = LoggerFactory.getLogger(ArrangeController.class);

  default void sendBackward() {
    LOG.info("Send to Back triggered");
    getPlugInPort().sendSelectionToBack();
  }

  default void bringForward() {
    LOG.info("Bring to Front triggered");
    getPlugInPort().bringSelectionToFront();
  }

  default void rotateSelection(int direction) {
    LOG.info("Rotate Selection triggered: " + direction);
    getPlugInPort().rotateSelection(direction);
  }

  default void group() {
    LOG.info("Group Selection triggered");
    getPlugInPort().groupSelectedComponents();
  }

  default void ungroup() {
    LOG.info("Ungroup Selection triggered");
    getPlugInPort().ungroupSelectedComponents();
  }

}
