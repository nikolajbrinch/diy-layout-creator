package org.diylc.app.menus.config;

import java.util.EnumSet;

import org.diylc.app.view.IPlugIn;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.ISwingUI;
import org.diylc.core.EventType;
import org.diylc.core.config.Configuration;

/**
 * Controls configuration menu.
 * 
 * @author Branislav Stojkovic
 */
public class ConfigMenuPlugin implements IPlugIn {

    private static final String CONFIG_MENU = "Config";

    private final ISwingUI swingUI;

    public ConfigMenuPlugin(ISwingUI swingUI) {
        this.swingUI = swingUI;
    }

    @Override
    public void connect(IPlugInPort plugInPort) {
        swingUI.injectMenuAction(new ConfigAction(plugInPort, "Auto-Create Pads", Configuration.Key.AUTO_CREATE_PADS, false), CONFIG_MENU);
        swingUI.injectMenuAction(new ConfigAction(plugInPort, "Auto-Edit Mode", Configuration.Key.AUTO_EDIT, true), CONFIG_MENU);
        swingUI.injectMenuAction(new ConfigAction(plugInPort, "Continuous Creation", Configuration.Key.CONTINUOUS_CREATION, false),
                CONFIG_MENU);
        swingUI.injectMenuAction(new ConfigAction(plugInPort, "Export Grid", Configuration.Key.EXPORT_GRID, false), CONFIG_MENU);
        swingUI.injectMenuAction(new ConfigAction(plugInPort, "Snap to Grid", Configuration.Key.SNAP_TO_GRID, true), CONFIG_MENU);
        swingUI.injectMenuAction(new ConfigAction(plugInPort, "Sticky Points", Configuration.Key.STICKY_POINTS, true), CONFIG_MENU);
    }

    @Override
    public EnumSet<EventType> getSubscribedEventTypes() {
        return EnumSet.noneOf(EventType.class);
    }

    @Override
    public void processMessage(EventType eventType, Object... params) {
    }
}
