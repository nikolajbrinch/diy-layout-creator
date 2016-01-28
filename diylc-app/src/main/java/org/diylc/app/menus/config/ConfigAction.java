package org.diylc.app.menus.config;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.IView;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigAction.class);

    private static final long serialVersionUID = 1L;

    private final IPlugInPort plugInPort;
    
    private final Configuration.Key key;

    public ConfigAction(IPlugInPort plugInPort, String title, Configuration.Key key, boolean defaultValue) {
        super();
        this.plugInPort = plugInPort;
        this.key = key;
        putValue(AbstractAction.NAME, title);
        putValue(IView.CHECK_BOX_MENU_ITEM, true);
        putValue(AbstractAction.SELECTED_KEY, Configuration.INSTANCE.getProperty(key, defaultValue));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOG.info(getValue(AbstractAction.NAME) + " triggered");
        Configuration.INSTANCE.setProperty(key, getValue(AbstractAction.SELECTED_KEY));
        plugInPort.refresh();
    }
}