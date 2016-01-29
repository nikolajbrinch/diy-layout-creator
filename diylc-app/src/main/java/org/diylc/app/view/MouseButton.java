package org.diylc.app.view;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

public enum MouseButton {

    NONE, LEFT, MIDDLE, RIGHT;

    public static MouseButton getButton(MouseEvent event) {
        MouseButton button = NONE;

        if (SwingUtilities.isLeftMouseButton(event)) {
            button = LEFT;
        } else if (SwingUtilities.isMiddleMouseButton(event)) {
            button = MIDDLE;
        } else if (SwingUtilities.isRightMouseButton(event)) {
            button = RIGHT;
        }

        return button;
    }

}
