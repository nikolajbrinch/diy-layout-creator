package org.diylc.app.actions;

import java.awt.event.ActionListener;

import javax.swing.KeyStroke;

import org.diylc.app.view.IView;

public class RadioButtonAction extends GenericAction {

    private static final long serialVersionUID = 1L;

    public RadioButtonAction(String name, String group, ActionListener actionListener) {
        this(name, null, group, actionListener);
    }

    public RadioButtonAction(String name, KeyStroke accelerator, String group, ActionListener actionListener) {
        super(name, null, accelerator, actionListener);
        putValue(IView.RADIO_BUTTON_GROUP_KEY, group);
    }

}
