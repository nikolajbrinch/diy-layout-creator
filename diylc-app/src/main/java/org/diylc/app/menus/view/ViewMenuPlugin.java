package org.diylc.app.menus.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.EnumSet;

import javax.swing.AbstractAction;

import org.diylc.app.Accelerators;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.IPlugIn;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.ISwingUI;
import org.diylc.core.EventType;
import org.diylc.core.Theme;
import org.diylc.core.config.Configuration;
import org.diylc.core.config.ConfigurationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Controls configuration menu.
 * 
 * @author Branislav Stojkovic
 */
public class ViewMenuPlugin implements IPlugIn {

    private static final Logger LOG = LoggerFactory.getLogger(ViewMenuPlugin.class);

    private static final String VIEW_MENU = "View";

    private static final String THEME_MENU = "Theme";

    private final ISwingUI swingUI;

    public ViewMenuPlugin(ISwingUI swingUI) {
        this.swingUI = swingUI;
    }

    @Override
    public void connect(IPlugInPort plugInPort) {
        swingUI.injectMenuAction(new ViewAction(plugInPort, "Anti-Aliasing", Configuration.Key.ANTI_ALIASING, true), VIEW_MENU);
        swingUI.injectMenuAction(new ViewAction(plugInPort, "Hi-Quality Rendering", Configuration.Key.HI_QUALITY_RENDER, false), VIEW_MENU);
        swingUI.injectMenuAction(null, VIEW_MENU);
        swingUI.injectMenuAction(new ViewAction(plugInPort, "Outline Mode", Configuration.Key.OUTLINE, false), VIEW_MENU);
        swingUI.injectMenuAction(new ViewAction(plugInPort, "Mouse Wheel Zoom", Configuration.Key.WHEEL_ZOOM, false), VIEW_MENU);
        swingUI.injectMenuAction(null, VIEW_MENU);

        ViewAction propertyPanelAction = new ViewAction(plugInPort, "Property Panel", Configuration.Key.PROPERTY_PANEL, false);
        propertyPanelAction.putValue(AbstractAction.ACCELERATOR_KEY, Accelerators.SHOW_PROPERTY_PANEL);

        Configuration.INSTANCE.addListener(Configuration.Key.PROPERTY_PANEL, new ConfigurationListener() {

            @Override
            public void onValueChanged(Object oldValue, Object newValue) {
                propertyPanelAction.putValue(AbstractAction.SELECTED_KEY, newValue);
            }
        });

        swingUI.injectMenuAction(propertyPanelAction, VIEW_MENU);

        File themeDir = new File("themes");
        if (themeDir.exists()) {
            XStream xStream = new XStream(new DomDriver());
            swingUI.injectSubmenu(THEME_MENU, AppIconLoader.Pens.getIcon(), VIEW_MENU);
            for (File file : themeDir.listFiles()) {
                if (file.getName().toLowerCase().endsWith(".xml")) {
                    try {
                        InputStream in = new FileInputStream(file);
                        Theme theme = (Theme) xStream.fromXML(in);
                        LOG.debug("Found theme: " + theme.getName());
                        swingUI.injectMenuAction(new ThemeAction(plugInPort, theme), THEME_MENU);
                    } catch (Exception e) {
                        LOG.error("Could not load theme file " + file.getName(), e);
                    }
                }
            }
        }
    }

    @Override
    public EnumSet<EventType> getSubscribedEventTypes() {
        return EnumSet.noneOf(EventType.class);
    }

    @Override
    public void processMessage(EventType eventType, Object... params) {
    }
}
