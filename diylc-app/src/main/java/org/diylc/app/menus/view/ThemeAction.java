package org.diylc.app.menus.view;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.IView;
import org.diylc.core.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThemeAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(ThemeAction.class);

    private static final long serialVersionUID = 1L;

    private final IPlugInPort plugInPort;
    
    private final Theme theme;

    public ThemeAction(IPlugInPort plugInPort, Theme theme) {
        super();
        this.plugInPort = plugInPort;
        this.theme = theme;
        putValue(AbstractAction.NAME, theme.getName());
        putValue(IView.RADIO_BUTTON_GROUP_KEY, "theme");
        putValue(AbstractAction.SELECTED_KEY, plugInPort.getSelectedTheme().getName().equals(theme.getName()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOG.info(getValue(AbstractAction.NAME) + " triggered");
        plugInPort.setSelectedTheme(theme);
    }
}