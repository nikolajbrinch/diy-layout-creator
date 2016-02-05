package org.diylc.app.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.diylc.core.utils.SystemUtils;

public class GenericAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    
    private ActionListener actionListener;

    public GenericAction(String name, ActionListener actionListener) {
        this(name, null, null, actionListener);
    }

    public GenericAction(String name, Icon icon, ActionListener actionListener) {
        this(name, icon, null, actionListener);
    }

    public GenericAction(String name, KeyStroke accelerator, ActionListener actionListener) {
        this(name, null, accelerator, actionListener);
    }

    public GenericAction(String name, Icon icon, KeyStroke accelerator, ActionListener actionListener) {
        super();
        this.actionListener = actionListener;
        putValue(AbstractAction.NAME, name);
        if (!SystemUtils.isMac()) {
            putValue(AbstractAction.SMALL_ICON, icon);
        }
        putValue(AbstractAction.ACCELERATOR_KEY, accelerator);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        this.actionListener.actionPerformed(e);
    }

}