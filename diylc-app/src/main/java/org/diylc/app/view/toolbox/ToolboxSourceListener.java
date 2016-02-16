package org.diylc.app.view.toolbox;

import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.JComponent;

import org.diylc.app.view.IPlugInPort;

/**
 * {@link DragSourceListener} for {@link CanvasPanel}.
 * 
 * @author Branislav Stojkovic
 */
class ToolboxSourceListener implements DragSourceListener {

    public ToolboxSourceListener(IPlugInPort presenter) {
        super();
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
        ((JComponent) dsde.getDragSourceContext().getComponent()).revalidate();
    }

    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragExit(DragSourceEvent dse) {
    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {
    }
}
