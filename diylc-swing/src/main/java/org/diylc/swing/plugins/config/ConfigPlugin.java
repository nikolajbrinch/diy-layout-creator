package org.diylc.swing.plugins.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.EnumSet;

import org.diylc.core.Theme;
import org.diylc.core.config.Configuration;
import org.diylc.images.CoreIconLoader;
import org.diylc.presenter.plugin.EventType;
import org.diylc.presenter.plugin.IPlugIn;
import org.diylc.presenter.plugin.IPlugInPort;
import org.diylc.swing.ActionFactory;
import org.diylc.swing.ISwingUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Controls configuration menu.
 * 
 * @author Branislav Stojkovic
 */
public class ConfigPlugin implements IPlugIn {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigPlugin.class);

	private static final String CONFIG_MENU = "Config";
	private static final String THEME_MENU = "Theme";

	private ISwingUI swingUI;

	public ConfigPlugin(ISwingUI swingUI) {
		super();
		this.swingUI = swingUI;
	}

	@Override
	public void connect(IPlugInPort plugInPort) {
		swingUI.injectMenuAction(ActionFactory.INSTANCE.createConfigAction(plugInPort,
				"Anti-Aliasing", Configuration.Key.ANTI_ALIASING, true), CONFIG_MENU);
		swingUI.injectMenuAction(ActionFactory.INSTANCE.createConfigAction(plugInPort,
				"Auto-Create Pads", Configuration.Key.AUTO_CREATE_PADS, false), CONFIG_MENU);
		swingUI.injectMenuAction(ActionFactory.INSTANCE.createConfigAction(plugInPort,
				"Auto-Edit Mode", Configuration.Key.AUTO_EDIT, true), CONFIG_MENU);
		swingUI.injectMenuAction(ActionFactory.INSTANCE.createConfigAction(plugInPort,
				"Continuous Creation", Configuration.Key.CONTINUOUS_CREATION, false), CONFIG_MENU);
		swingUI.injectMenuAction(ActionFactory.INSTANCE.createConfigAction(plugInPort,
				"Export Grid", Configuration.Key.EXPORT_GRID, false), CONFIG_MENU);
		swingUI.injectMenuAction(ActionFactory.INSTANCE.createConfigAction(plugInPort,
				"Hi-Quality Rendering", Configuration.Key.HI_QUALITY_RENDER, false), CONFIG_MENU);
		swingUI.injectMenuAction(ActionFactory.INSTANCE.createConfigAction(plugInPort,
				"Mouse Wheel Zoom", Configuration.Key.WHEEL_ZOOM, false), CONFIG_MENU);
		swingUI.injectMenuAction(ActionFactory.INSTANCE.createConfigAction(plugInPort,
				"Outline Mode", Configuration.Key.OUTLINE, false), CONFIG_MENU);
		swingUI.injectMenuAction(ActionFactory.INSTANCE.createConfigAction(plugInPort,
				"Snap to Grid", Configuration.Key.SNAP_TO_GRID, true), CONFIG_MENU);
		swingUI.injectMenuAction(ActionFactory.INSTANCE.createConfigAction(plugInPort,
				"Sticky Points", Configuration.Key.STICKY_POINTS, true), CONFIG_MENU);

		File themeDir = new File("themes");
		if (themeDir.exists()) {
			XStream xStream = new XStream(new DomDriver());
			swingUI.injectSubmenu(THEME_MENU, CoreIconLoader.Pens.getIcon(), CONFIG_MENU);
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
