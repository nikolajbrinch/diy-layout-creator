package org.diylc.app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ArrangeController extends MenuController {
    
    static final Logger LOG = LoggerFactory.getLogger(ArrangeController.class);

    default void sendBackward() {
        LOG.info("Send to Back triggered");
        getController().sendSelectionToBack();
    }
    
    default void bringForward() {
        LOG.info("Bring to Front triggered");
        getController().bringSelectionToFront();
    }

    default void rotateSelection(int direction) {
        LOG.info("Rotate Selection triggered: " + direction);
        getController().rotateSelection(direction);
    }

    default void group() {
        LOG.info("Group Selection triggered");
        getController().groupSelectedComponents();
    }

    default void ungroup() {
        LOG.info("Ungroup Selection triggered");
        getController().ungroupSelectedComponents();
    }
    
}
