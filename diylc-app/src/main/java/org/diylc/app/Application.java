package org.diylc.app;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.diylc.app.dialogs.TemplateDialog;
import org.diylc.app.platform.Platform;
import org.diylc.app.platform.PreferencesEvent;
import org.diylc.app.platform.QuitEvent;
import org.diylc.app.view.MainFrame;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(DIYLCStarter.class);

    private static final String SCRIPT_RUN = "org.diylc.scriptRun";

    private MainFrame mainFrame = null;

    public void run(String[] args) {
        Platform.getPlatform().setup();

        LOG.info("Starting DIYLC with working directory " + System.getProperty("user.dir"));


        setUncaughtExceptionHandler();

        warnScriptRun();

        if (showWindow()) {
            if (args.length > 0) {
                openProject(Paths.get(args[0]));
            } else {
                showTemplates();
            }

            Platform.getPlatform().setPreferencesHandler((PreferencesEvent e) -> LOG.debug("Show preferences dialog"));
            Platform.getPlatform().setQuitHandler((QuitEvent e) -> mainFrame.exit());
        }

        /*
         * FIXME: This definately has to go, as this could break everything.
         * Structured definition of customizability needs to be in place.
         */
        injectProperties();
    }

    private void injectProperties() {
        // Properties properties = new Properties();
        // try {
        // LOG.info("Injecting default properties.");
        // File f = new File("config.properties");
        // if (f.exists()) {
        // properties.load(new FileInputStream(f));
        // PropertyInjector.injectProperties(properties);
        // }
        // } catch (Exception e) {
        // LOG.error("Could not read config.properties file", e);
        // } }
    }

    private void showTemplates() {
        boolean showTemplates = Configuration.INSTANCE.getShowTemplates();
        if (showTemplates) {
            TemplateDialog templateDialog = new TemplateDialog(mainFrame, mainFrame.getPresenter());
            if (!templateDialog.getFiles().isEmpty()) {
                templateDialog.setVisible(true);
            }
        }
    }

    private void warnScriptRun() {
        String val = System.getProperty(SCRIPT_RUN);
        if (!"true".equals(val)) {
            int response = JOptionPane.showConfirmDialog(null, "It is not recommended to run DIYLC by clicking on the diylc.jar file.\n"
                    + "Please use diylc.exe on Windows or run.sh on OSX/Linux to ensure the best\n"
                    + "performance and reliability. Do you want to continue?", "DIYLC", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (response != JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    private void setUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
        System.setProperty("sun.awt.exception.handler", DefaultUncaughtExceptionHandler.class.getName());
    }

    private void openProject(Path path) {
        try {
            mainFrame.openProject(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean showWindow() {
        mainFrame = new MainFrame();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                mainFrame.setVisible(true);
            }
        });

        return mainFrame != null;
    }
}
