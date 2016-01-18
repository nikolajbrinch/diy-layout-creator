package org.diylc.core.config;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.diylc.core.ConfigurationManager;
import org.diylc.core.LRU;
import org.diylc.core.Template;
import org.diylc.core.Theme;
import org.diylc.core.utils.Constants;

public enum Configuration {

	INSTANCE;

	private Map<Key, Object> configuration = new HashMap<Key, Object>();
	private Map<Key, List<ConfigurationListener>> listeners = new HashMap<Key, List<ConfigurationListener>>();

	public static enum Key {
		SHOW_TEMPLATES("showTemplatesAtStartup"), 
		THEME("theme"), 
		LAST_PATH("lastPath"), 
		WINDOW_BOUNDS("windowBounds"), 
		ABNORMAL_EXIT("abnormalExit"), 
		RECENT_COMPONENTS("recentComponents"), 
		METRIC("metric"), 
		LRU("lru"),
		EXPORT_GRID("exportGrid"), 
		OUTLINE("outline"), 
		HI_QUALITY_RENDER("hiQualityRender"), 
		WHEEL_ZOOM("wheelZoom"), 
		ANTI_ALIASING("antiAliasing"), 
		AUTO_CREATE_PADS("autoCreatePads"), 
		TEMPLATES("templates"), 
		STICKY_POINTS("stickyPoints"), 
		SNAP_TO_GRID("snapToGrid"), 
		AUTO_EDIT("autoEdit"), 
		CONTINUOUS_CREATION("continuousCreation"), 
		OBJECT_PROPERTIES("objectProperties"), 
		PROJECT_PROPERTIES("projectProperties"), 
		COMPONENT_DIRECTORIES("componentDirectories")
		;

		private String keyValue;

		private Key(String keyValue) {
			this.keyValue = keyValue;
		}
	}

	public boolean getShowTemplates() {
		return getBooleanProperty(Key.SHOW_TEMPLATES, true);
	}

	public void setShowTemplates(boolean showTemplates) {
		setBooleanProperty(Key.SHOW_TEMPLATES, showTemplates);
	}

	public Theme getTheme() {
		return getObjectProperty(Key.THEME, Constants.DEFAULT_THEME);
	}

	public void setTheme(Theme theme) {
		setObjectProperty(Key.THEME, theme);
	}

	public Path getLastPath() {
		return Paths.get(getStringProperty(Key.LAST_PATH, null));
	}

	public void setLastPath(Path path) {
		setStringProperty(Key.LAST_PATH, path.toAbsolutePath().toString());
	}

	public boolean getAbnormalExit() {
		return getBooleanProperty(Key.ABNORMAL_EXIT, false);
	}

	public void setAbnormalExit(boolean abnormalExit) {
		setBooleanProperty(Key.ABNORMAL_EXIT, abnormalExit);
	}

	public List<String> getRecentComponents() {
		return getObjectProperty(Key.RECENT_COMPONENTS, Collections.emptyList());
	}

	public void setRecentComponents(List<String> recentComponents) {
		setObjectProperty(Key.RECENT_COMPONENTS, recentComponents);
	}

	public boolean getMetric() {
		return getBooleanProperty(Key.METRIC, true);
	}

	public void setMetric(boolean metric) {
		setBooleanProperty(Key.METRIC, metric);
	}

	public boolean getExportGrid() {
		return getBooleanProperty(Key.EXPORT_GRID, false);
	}

	public boolean getOutline() {
		return getBooleanProperty(Key.OUTLINE, false);
	}

	public boolean getHiQualityRender() {
		return getBooleanProperty(Key.HI_QUALITY_RENDER, false);
	}
	
	public boolean getWheelZoom() {
		return getBooleanProperty(Key.WHEEL_ZOOM, false);
	}

	public boolean getAntiAliasing() {
		return getBooleanProperty(Key.ANTI_ALIASING, true);
	}

	public boolean getAutoCreatePads() {
		return getBooleanProperty(Key.AUTO_CREATE_PADS, false);
	}

	public boolean getStickyPoints() {
		return getBooleanProperty(Key.STICKY_POINTS, true);
	}
	
	public boolean getSnapToGrip() {
		return getBooleanProperty(Key.SNAP_TO_GRID, true);
	}

	public boolean getAutoEdit() {
		return getBooleanProperty(Key.AUTO_EDIT, false);
	}

	public boolean getContinuousCreation() {
		return getBooleanProperty(Key.CONTINUOUS_CREATION, false);
	}

	public Map<String, List<Template>> getTemplates() {
		return getObjectProperty(Key.TEMPLATES, null);
	}

	public void setTemplates(Map<String, List<Template>> templates) {
		setObjectProperty(Key.TEMPLATES, templates);
	}

	public WindowBounds getWindowBounds() {
		Map<String, Integer> defaultWindowBounds = new HashMap<>();
		defaultWindowBounds.put("x", 100);
		defaultWindowBounds.put("y", 100);
		defaultWindowBounds.put("width", 800);
		defaultWindowBounds.put("height", 600);
		defaultWindowBounds.put("extendedState", JFrame.NORMAL);

		Map<String, Integer> windowBounds = (Map<String, Integer>) getObjectProperty(
				Key.WINDOW_BOUNDS, defaultWindowBounds);

		for (Map.Entry<String, Integer> entry : defaultWindowBounds.entrySet()) {
			if (windowBounds.get(entry.getKey()) == null) {
				windowBounds.put(entry.getKey(), entry.getValue());
			}
		}

		return new WindowBounds(windowBounds.get("x"), windowBounds.get("y"),
				windowBounds.get("width"), windowBounds.get("height"),
				windowBounds.get("extendedState"));
	}

	public void setWindowBounds(WindowBounds windowBounds) {
		Map<String, Integer> windowBoundsMap = new HashMap<>();
		windowBoundsMap.put("x", windowBounds.getX());
		windowBoundsMap.put("y", windowBounds.getY());
		windowBoundsMap.put("width", windowBounds.getWidth());
		windowBoundsMap.put("height", windowBounds.getHeight());
		windowBoundsMap.put("extendedState", windowBounds.getExtendedState());

		setObjectProperty(Key.WINDOW_BOUNDS, windowBoundsMap);
	}

	public LRU<Path> getLru() {
		LRU<Path> lru = new LRU<Path>(15);

		List<String> paths = getObjectProperty(Key.LRU, Collections.emptyList());

		Collections.reverse(paths);

		for (String file : paths) {
			lru.addItem(Paths.get(file));
		}

		return lru;
	}

	public void setLru(LRU<Path> lru) {
		List<String> paths = new ArrayList<>();

		for (Path path : lru.getItems()) {
			paths.add(path.toAbsolutePath().normalize().toString());
		}

		setObjectProperty(Key.LRU, paths);
	}

	public Map<String, Object> getProjectProperties() {
		Map<String, Object> projectProperties = getObjectProperty(Key.PROJECT_PROPERTIES, null);
		
		if (projectProperties == null) {
			projectProperties = new HashMap<String, Object>();
		}
		
		return projectProperties;
	}
	
	public void setProjectProperties(Map<String, Object> projectProperties) {
		setObjectProperty(Key.PROJECT_PROPERTIES, projectProperties);
	}

	public Map<String, Map<String, Object>> getObjectProperties() {
		Map<String, Map<String, Object>> objectProperties = getObjectProperty(Key.OBJECT_PROPERTIES, null);
		
		if (objectProperties == null) {
			objectProperties = new HashMap<String, Map<String, Object>>();
		}
		
		return objectProperties;
	}

	public void setObjectProperties(Map<String, Map<String, Object>> objectProperties) {
		setObjectProperty(Key.OBJECT_PROPERTIES, objectProperties);
	}

    public Path[] getComponentDirectories() {
        return getObjectProperty(Key.COMPONENT_DIRECTORIES, new Path[] {
                Paths.get(System.getProperty("user.home"), ".diylc/lib/components")
        });
    }

	
	public <T> T getProperty(Key key, T defaultValue) {
		return getObjectProperty(key, defaultValue);
	}

	public void setProperty(Key key, Object value) {
		setPropertyInternal(key, value);
	}

	public void addListener(Key key, ConfigurationListener keyListener) {
		List<ConfigurationListener> keyListeners = listeners.get(key);
		
		if (keyListeners == null) {
			keyListeners = new ArrayList<ConfigurationListener>();
			listeners.put(key, keyListeners);
		}
		
		keyListeners.add(keyListener);
	}

	private boolean getBooleanProperty(Key key, boolean defaultValue) {
		return getObjectProperty(key, defaultValue);
	}

	private void setBooleanProperty(Key key, boolean value) {
		setProperty(key, value);
	}

	private String getStringProperty(Key key, String defaultValue) {
		return getObjectProperty(key, defaultValue);
	}

	private void setStringProperty(Key key, String value) {
		setProperty(key, value);
	}

	@SuppressWarnings("unchecked")
	private <T> T getObjectProperty(Key key, T defaultValue) {
		return (T) (configuration.containsKey(key) ? configuration.get(key) : ConfigurationManager.getInstance().readObject(key.keyValue,
				defaultValue));
	}

	private void setObjectProperty(Key key, Object value) {
		setProperty(key, value);
	}

	private void setPropertyInternal(Key key, Object value) {
		Object oldValue = configuration.put(key, value);

		ConfigurationManager.getInstance().writeValue(key.keyValue, value);
		
		if (listeners.containsKey(key)) {
			for (ConfigurationListener keyListener : listeners.get(key)) {
				keyListener.onValueChanged(oldValue, value);
			}
		}
	}

}
