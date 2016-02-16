package org.diylc.app.view.toolbox;

import java.awt.Cursor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;

import org.diylc.app.view.IPlugInPort;

/**
 * {@link DragGestureListener} for {@link CanvasPanel}.
 * 
 * @author Branislav Stojkovic
 */
class ToolboxGestureListener implements DragGestureListener {

    private IPlugInPort presenter;
    private String className;

    public ToolboxGestureListener(IPlugInPort presenter, String className) {
        super();
        this.presenter = presenter;
        this.className = className;
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        dge.startDrag(Cursor.getDefaultCursor(), new StringSelection(className), new ToolboxSourceListener(presenter));
    }
}
