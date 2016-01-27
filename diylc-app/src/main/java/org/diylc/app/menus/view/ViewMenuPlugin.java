package org.diylc.app.menus.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.EnumSet;

import org.diylc.app.actions.ActionFactory;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.EventType;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.window.IPlugIn;
import org.diylc.app.window.ISwingUI;
import org.diylc.core.Theme;
import org.diylc.core.config.Configuration;
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

	private ISwingUI swingUI;

	public ViewMenuPlugin(ISwingUI swingUI) {
		super();
		this.swingUI = swingUI;
	}

	@Override
	public void connect(IPlugInPort plugInPort) {
		swingUI.injectMenuAction(ActionFactory.INSTANCE.createViewAction(plugInPort,
				"Anti-Aliasing", Configuration.Key.ANTI_ALIASING, true), VIEW_MENU);
		swingUI.injectMenuAction(ActionFactory.INSTANCE.createViewAction(plugInPort,
				"Hi-Quality Rendering", Configuration.Key.HI_QUALITY_RENDER, false), VIEW_MENU);
		swingUI.injectMenuAction(ActionFactory.INSTANCE.createViewAction(plugInPort,
				"Mouse Wheel Zoom", Configuration.Key.WHEEL_ZOOM, false), VIEW_MENU);
		swingUI.injectMenuAction(ActionFactory.INSTANCE.createViewAction(plugInPort,
				"Outline Mode", Configuration.Key.OUTLINE, false), VIEW_MENU);
        swingUI.injectMenuAction(ActionFactory.INSTANCE.createViewAction(plugInPort,
                "Property Panel", Configuration.Key.PROPERTY_PANEL, false), VIEW_MENU);

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
						swingUI.injectMenuAction(ActionFactory.INSTANCE.createThemeAction(
								plugInPort, theme), THEME_MENU);
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
