package org.diylc.app;

import java.io.File;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import org.diylc.app.view.EventType;
import org.diylc.app.view.IPlugIn;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.IView;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoSavePlugin implements IPlugIn {

    private static final Logger LOG = LoggerFactory.getLogger(AutoSavePlugin.class);

    private static final String AUTO_SAVE_FILE_NAME = "autoSave.diy";

	protected static final long autoSaveFrequency = 60 * 1000;

	private ExecutorService executor;

	private IPlugInPort plugInPort;
	private IView view;
	private long lastSave = 0;

	public AutoSavePlugin(IView view) {
		this.view = view;
		executor = Executors.newSingleThreadExecutor();
	}

	@Override
	public void connect(IPlugInPort plugInPort) {
		this.plugInPort = plugInPort;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				boolean wasAbnormal = Configuration.INSTANCE.getAbnormalExit();
				if (wasAbnormal && new File(AUTO_SAVE_FILE_NAME).exists()) {
					int decision = view
							.showConfirmDialog(
									"It appears that aplication was not closed normally in the previous session. Do you want to open the last auto-saved file?",
									"Auto-Save", IView.YES_NO_OPTION,
									IView.QUESTION_MESSAGE);
					if (decision == IView.YES_OPTION) {
						try {
                            AutoSavePlugin.this.plugInPort
                            		.loadProjectFromFile(Paths.get(AUTO_SAVE_FILE_NAME));
                        } catch (Exception e) {
                            LOG.warn("Error loading autoSave.diy file", e);
                        }
					}
				}
				/* 
				 * Set abnormal flag to true, GUI side of the app must flip to
				 * false when app closes regularly.
				 */
				Configuration.INSTANCE.setAbnormalExit(true);
			}
		});
	}

	@Override
	public EnumSet<EventType> getSubscribedEventTypes() {
		return EnumSet.of(EventType.PROJECT_MODIFIED);
	}

	@Override
	public void processMessage(EventType eventType, Object... params) {
		if (eventType == EventType.PROJECT_MODIFIED) {
			if (System.currentTimeMillis() - lastSave > autoSaveFrequency) {
				executor.execute(new Runnable() {

					@Override
					public void run() {
						Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

						lastSave = System.currentTimeMillis();
						plugInPort.saveProjectToFile(Paths.get(AUTO_SAVE_FILE_NAME), true);
					}
				});
			}
		}
	}
}
