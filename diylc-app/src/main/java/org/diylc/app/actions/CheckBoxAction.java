package org.diylc.app.actions;

import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.diylc.app.view.IView;

public class CheckBoxAction extends GenericAction {

    private static final long serialVersionUID = 1L;

    public CheckBoxAction(String name, Object value, ActionListener actionListener) {
        this(name, null, value, actionListener);
    }

    public CheckBoxAction(String name, KeyStroke accelerator, Object value, ActionListener actionListener) {
        super(name, null, accelerator, actionListener);
        putValue(IView.CHECK_BOX_MENU_ITEM, true);
        putValue(AbstractAction.SELECTED_KEY, value);
    }

}
