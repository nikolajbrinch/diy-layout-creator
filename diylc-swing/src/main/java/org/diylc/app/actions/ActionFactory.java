package org.diylc.app.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.diylc.app.IPlugInPort;
import org.diylc.app.IView;
import org.diylc.core.Theme;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ActionFactory {

    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(ActionFactory.class);

    private ActionFactory() {
    }

    // Config actions.

    public ConfigAction createConfigAction(IPlugInPort plugInPort, String title, Configuration.Key key, boolean defaultValue) {
        return new ConfigAction(plugInPort, title, key, defaultValue);
    }

    public ThemeAction createThemeAction(IPlugInPort plugInPort, Theme theme) {
        return new ThemeAction(plugInPort, theme);
    }

    public static class ConfigAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;
        private Configuration.Key key;

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

    public static class ThemeAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;
        private Theme theme;

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


}
