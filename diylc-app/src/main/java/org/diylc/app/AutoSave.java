package org.diylc.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.diylc.app.controllers.ApplicationController;
import org.diylc.app.view.IView;
import org.diylc.core.EventType;
import org.diylc.core.config.Configuration;
import org.diylc.core.events.EventListener;
import org.diylc.core.events.EventReciever;
import org.diylc.core.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoSave implements EventListener<EventType> {

    private static final Logger LOG = LoggerFactory.getLogger(AutoSave.class);

    private static final String AUTO_SAVE_FILE_NAME = "autoSave.diy";

    protected static final long autoSaveFrequency = 60 * 1000;

    private final EventReciever<EventType> eventReciever = new EventReciever<EventType>();

    private final ApplicationController applicationController;

    private final Path autoSaveDirectory;

    private long lastSave = 0;

    private Path autoSaveFile;

    public AutoSave(ApplicationController applicationController) throws IOException {
        this.applicationController = applicationController;
        this.autoSaveDirectory = SystemUtils.getConfigDirectory().toPath();
        this.autoSaveFile = Paths.get(autoSaveDirectory.toString(), AUTO_SAVE_FILE_NAME);

        try {
            Path testPath = Paths.get(autoSaveDirectory.toString(), "test.tmp");
            BufferedWriter writer = Files.newBufferedWriter(testPath);
            writer.write("This is a test");
            writer.close();
            Files.delete(testPath);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "The current user does not have permissions to access folder " + new File(".").getAbsolutePath()
                    + ".\nAuto-save feature will not be available, contact your system administrator.", "Warning", IView.WARNING_MESSAGE);
        }

        eventReciever.registerListener(EnumSet.of(EventType.PROJECT_MODIFIED), this);

        init();
    }

    private void init() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                boolean wasAbnormal = Configuration.INSTANCE.getAbnormalExit();

                if (wasAbnormal && Files.exists(autoSaveFile)) {
                    int decision = JOptionPane
                            .showConfirmDialog(
                                    null,
                                    "It appears that aplication was not closed normally in the previous session. Do you want to open the last auto-saved file?",
                                    "Auto-Save", IView.YES_NO_OPTION, IView.QUESTION_MESSAGE);
                    if (decision == IView.YES_OPTION) {
                        try {
                            applicationController.open(autoSaveFile);
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
    public void processEvent(EventType eventType, Object... params) {
        if (eventType == EventType.PROJECT_MODIFIED) {
            if (System.currentTimeMillis() - lastSave > autoSaveFrequency) {
                new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

                        lastSave = System.currentTimeMillis();
                        // applicationController.save(autoSaveFile, true);

                        return null;
                    }

                }.execute();
            }
        }
    }
}
