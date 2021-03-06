package org.diylc.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.diylc.core.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Utility that reads and writes configuration to an XML file. Each
 * configuration item should have a unique name. To write to the configuration
 * use {@link #writeValue(String, Object)}. Use <code>readXYZ</code> methods
 * where XYZ stands for specific data types.
 * 
 * @author Branislav Stojkovic
 */
public class ConfigurationManager {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigurationManager.class);

	private static ConfigurationManager instance;

	private XStream xStream;
	
	private Map<String, Object> configuration;

	public static ConfigurationManager getInstance() {
		if (instance == null) {
			instance = new ConfigurationManager();
		}
		return instance;
	}

	private ConfigurationManager() {
		this.xStream = new XStream(new DomDriver());
		xStream.registerConverter(new IconImageConverter());
		initializeConfiguration();
	}

	@SuppressWarnings("unchecked")
	private void initializeConfiguration() {
		LOG.debug("Initializing configuration");
		
		File configFile = SystemUtils.getConfigFile("config.xml");
		
		
		try {
			FileInputStream in = new FileInputStream(configFile);
			configuration = (Map<String, Object>) xStream.fromXML(in);
			in.close();
		} catch (Exception e) {
			configuration = new HashMap<String, Object>();
		}
	}

	public void saveConfigration() {
		LOG.debug("Saving configuration");
		File configFile = SystemUtils.getConfigFile("config.xml");
		try {
			FileOutputStream out = new FileOutputStream(configFile);
			xStream.toXML(configuration, out);
			out.close();
		} catch (Exception e) {
			LOG.error("Could not save configuration: " + e.getMessage());
		}
	}

	public boolean readBoolean(String key, boolean defaultValue) {
		if (configuration.containsKey(key)) {
			return (Boolean) configuration.get(key);
		} else {
			return defaultValue;
		}
	}

	public String readString(String key, String defaultValue) {
		if (configuration.containsKey(key)) {
			return (String) configuration.get(key);
		} else {
			return defaultValue;
		}
	}

	public int readInt(String key, int defaultValue) {
		if (configuration.containsKey(key)) {
			return (Integer) configuration.get(key);
		} else {
			return defaultValue;
		}
	}

	public float readFloat(String key, float defaultValue) {
		if (configuration.containsKey(key)) {
			return (Float) configuration.get(key);
		} else {
			return defaultValue;
		}
	}

	public double readDouble(String key, double defaultValue) {
		if (configuration.containsKey(key)) {
			return (Double) configuration.get(key);
		} else {
			return defaultValue;
		}
	}

	public Object readObject(String key, Object defaultValue) {
		if (configuration.containsKey(key)) {
			return configuration.get(key);
		} else {
			return defaultValue;
		}
	}

	public void writeValue(String key, Object value) {
		configuration.put(key, value);
		saveConfigration();
	}
}
