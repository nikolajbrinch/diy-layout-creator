package org.diylc.appframework.miscutils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.FileManager;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Integers;

/**
 * This is a customized log4j appender, which will create a new file for every
 * run of the application.
 */
@Plugin(name = "NewLogForEachRunFileAppender", category = "File", elementType = "appender", printObject = true)
public class NewLogForEachRunFileAppender extends AbstractOutputStreamAppender<FileManager> {
	
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private final String fileName;
    private final Advertiser advertiser;
    private Object advertisement;

    private NewLogForEachRunFileAppender(final String name, final Layout<? extends Serializable> layout, final Filter filter,
            final FileManager manager, final String filename, final boolean ignoreExceptions,
            final boolean immediateFlush, final Advertiser advertiser) {
        
        super(name, layout, filter, ignoreExceptions, immediateFlush, manager);
        if (advertiser != null) {
            final Map<String, String> configuration = new HashMap<>(layout.getContentFormat());
            configuration.putAll(manager.getContentFormat());
            configuration.put("contentType", layout.getContentType());
            configuration.put("name", name);
            advertisement = advertiser.advertise(configuration);
        }
        this.fileName = filename;
        this.advertiser = advertiser;
    }

    @Override
    public void stop() {
        super.stop();
        if (advertiser != null) {
            advertiser.unadvertise(advertisement);
        }
    }

    /**
     * Returns the file name this appender is associated with.
     * @return The File name.
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Create a File Appender.
     * @param fileName The name and path of the file.
     * @param append "True" if the file should be appended to, "false" if it should be overwritten.
     * The default is "true".
     * @param locking "True" if the file should be locked. The default is "false".
     * @param name The name of the Appender.
     * @param immediateFlush "true" if the contents should be flushed on every write, "false" otherwise. The default
     * is "true".
     * @param ignore If {@code "true"} (default) exceptions encountered when appending events are logged; otherwise
     *               they are propagated to the caller.
     * @param bufferedIo "true" if I/O should be buffered, "false" otherwise. The default is "true".
     * @param bufferSizeStr buffer size for buffered IO (default is 8192).
     * @param layout The layout to use to format the event. If no layout is provided the default PatternLayout
     * will be used.
     * @param filter The filter, if any, to use.
     * @param advertise "true" if the appender configuration should be advertised, "false" otherwise.
     * @param advertiseUri The advertised URI which can be used to retrieve the file contents.
     * @param config The Configuration
     * @return The FileAppender.
     */
    @PluginFactory
    public static NewLogForEachRunFileAppender createAppender(
            // @formatter:off
            @PluginAttribute("fileName") final String fileName,
            @PluginAttribute("append") final String append,
            @PluginAttribute("locking") final String locking,
            @PluginAttribute("name") final String name,
            @PluginAttribute("immediateFlush") final String immediateFlush,
            @PluginAttribute("ignoreExceptions") final String ignore,
            @PluginAttribute("bufferedIo") final String bufferedIo,
            @PluginAttribute("bufferSize") final String bufferSizeStr,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("advertise") final String advertise,
            @PluginAttribute("advertiseUri") final String advertiseUri,
            @PluginConfiguration final Configuration config) {
        // @formatter:on
        final boolean isAppend = Booleans.parseBoolean(append, true);
        final boolean isLocking = Boolean.parseBoolean(locking);
        boolean isBuffered = Booleans.parseBoolean(bufferedIo, true);
        final boolean isAdvertise = Boolean.parseBoolean(advertise);
        if (isLocking && isBuffered) {
            if (bufferedIo != null) {
                LOGGER.warn("Locking and buffering are mutually exclusive. No buffering will occur for " + fileName);
            }
            isBuffered = false;
        }
        final int bufferSize = Integers.parseInt(bufferSizeStr, DEFAULT_BUFFER_SIZE);
        if (!isBuffered && bufferSize > 0) {
            LOGGER.warn("The bufferSize is set to {} but bufferedIO is not true: {}", bufferSize, bufferedIo);
        }
        final boolean isFlush = Booleans.parseBoolean(immediateFlush, true);
        final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);

        if (name == null) {
            LOGGER.error("No name provided for FileAppender");
            return null;
        }

        if (fileName == null) {
            LOGGER.error("No filename provided for FileAppender with name "  + name);
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        final FileManager manager = FileManager.getFileManager(fileName, isAppend, isLocking, isBuffered, advertiseUri,
            layout, bufferSize);
        if (manager == null) {
            return null;
        }

        return new NewLogForEachRunFileAppender(name, layout, filter, manager, fileName, ignoreExceptions, isFlush,
                isAdvertise ? config.getAdvertiser() : null);
    }
	
//	public NewLogForEachRunFileAppender() {
//	}
//
//	public NewLogForEachRunFileAppender(Layout layout, String filename, boolean append,
//			boolean bufferedIO, int bufferSize) throws IOException {
//		super(layout, filename, append, bufferedIO, bufferSize);
//	}
//
//	public NewLogForEachRunFileAppender(Layout layout, String filename, boolean append)
//			throws IOException {
//		super(layout, filename, append);
//	}
//
//	public NewLogForEachRunFileAppender(Layout layout, String filename) throws IOException {
//		super(layout, filename);
//	}
//
//	public void activateOptions() {
//		if (fileName != null) {
//			try {
//				fileName = getNewLogFileName();
//				setFile(fileName, fileAppend, bufferedIO, bufferSize);
//			} catch (Exception e) {
//				errorHandler.error("Error while activating log options", e,
//						ErrorCode.FILE_OPEN_FAILURE);
//			}
//		}
//	}
//
//	private String getNewLogFileName() {
//		if (fileName != null) {
//			String toret = "";
//			final String DOT = ".";
//			final String HIPHEN = "-";
//			final File logFile = new File(fileName);
//			final String fileName = logFile.getName();
//			String newFileName = "";
//
//			final int dotIndex = fileName.indexOf(DOT);
//			if (dotIndex != -1) {
//				// the file name has an extension. so, insert the time stamp
//				// between the file name and the extension
//				newFileName = fileName.substring(0, dotIndex) + HIPHEN + new java.util.Date() + DOT
//						+ fileName.substring(dotIndex + 1);
//			} else {
//				// the file name has no extension. So, just append the timestamp
//				// at the end.
//				newFileName = fileName + HIPHEN + new java.util.Date();
//			}
//			if (logFile.getParent() != null) {
//				toret = logFile.getParent() + File.separator + newFileName;
//			} else {
//				toret = newFileName;
//			}
//			return toret.replaceAll(":", ".");
//		}
//		return null;
//	}
}