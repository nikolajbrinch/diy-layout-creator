package org.diylc.app;

import java.nio.file.Paths;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.diylc.app.view.Presenter;
import org.diylc.core.SystemUtils;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class that runs DIYLC.
 * 
 * @author Branislav Stojkovic
 * 
 * @see Presenter
 * @see MainFrame
 */
public class DIYLCStarter {

	private static final Logger LOG = LoggerFactory
			.getLogger(DIYLCStarter.class);

	private static final String SCRIPT_RUN = "org.diylc.scriptRun";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        if (SystemUtils.isMac()) {
            MacApplicationHandler.setupMacApplication();
        }  
        
		LOG.debug("Java version: " + System.getProperty("java.runtime.version")
				+ " by " + System.getProperty("java.vm.vendor"));
		LOG.debug("OS: " + System.getProperty("os.name") + " "
				+ System.getProperty("os.version"));

		LOG.info("Starting DIYLC with working directory "
				+ System.getProperty("user.dir"));

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LOG.error("Could not set Look&Feel", e);
		}

		String val = System.getProperty(SCRIPT_RUN);
		if (!"true".equals(val)) {
			int response = JOptionPane
					.showConfirmDialog(
							null,
							"It is not recommended to run DIYLC by clicking on the diylc.jar file.\n"
									+ "Please use diylc.exe on Windows or run.sh on OSX/Linux to ensure the best\n"
									+ "performance and reliability. Do you want to continue?",
							"DIYLC", JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);
			if (response != JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}

		Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
		System.setProperty("sun.awt.exception.handler",
				DefaultUncaughtExceptionHandler.class.getName());

		final MainFrame mainFrame = new MainFrame();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				mainFrame.setVisible(true);
			}
		});

		if (mainFrame != null) {
			if (args.length > 0) {
				try {
                    mainFrame.getPresenter().loadProjectFromFile(Paths.get(args[0]));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
			} else {
				boolean showTemplates = Configuration.INSTANCE.getShowTemplates();
				if (showTemplates) {
					TemplateDialog templateDialog = new TemplateDialog(
							mainFrame, mainFrame.getPresenter());
					if (!templateDialog.getFiles().isEmpty()) {
						templateDialog.setVisible(true);
					}
				}
			}
		}

		/*
		 * FIXME: This definately has to go, as this could break everything.
		 * Structured definition of customizability needs to be in place.
		 */
//		Properties properties = new Properties();
//		try {
//			LOG.info("Injecting default properties.");
//			File f = new File("config.properties");
//			if (f.exists()) {
//				properties.load(new FileInputStream(f));
//				PropertyInjector.injectProperties(properties);
//			}
//		} catch (Exception e) {
//			LOG.error("Could not read config.properties file", e);
//		}
	}
}
