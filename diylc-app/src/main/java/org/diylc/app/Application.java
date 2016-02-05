package org.diylc.app;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.diylc.app.actions.GenericAction;
import org.diylc.app.controllers.ApplicationController;
import org.diylc.app.model.DrawingModel;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.utils.async.Async;
import org.diylc.app.view.ISwingUI;
import org.diylc.app.view.IView;
import org.diylc.app.view.Presenter;
import org.diylc.app.view.View;
import org.diylc.app.view.dialogs.DialogFactory;
import org.diylc.app.view.menus.MenuConstants;
import org.diylc.components.registry.ComponentRegistry;
import org.diylc.core.BootUtils;
import org.diylc.core.LRU;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.config.Configuration;
import org.diylc.core.platform.DefaultQuitResponse;
import org.diylc.core.platform.Platform;
import org.diylc.core.platform.PreferencesEvent;
import org.diylc.core.platform.QuitEvent;
import org.diylc.core.platform.QuitHandler;
import org.diylc.core.platform.QuitResponse;
import org.diylc.core.platform.RestartQuitResponse;
import org.diylc.core.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nikolajbrinch@gmail.com
 */
public class Application implements ApplicationController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private static final String SCRIPT_RUN = "org.diylc.scriptRun";

    private final List<Drawing> drawings = new ArrayList<>();

    private LRU<Path> lru = new LRU<Path>(15);

    private Drawing currentDrawing;

    @SuppressWarnings("unused")
    private AutoSave autoSave;

    public void run(String[] args) {
        Platform.getPlatform().setup();

        DiylcSplashScreen splashScreen = new DiylcSplashScreen();

        Path[] componentDirectories = Configuration.INSTANCE.getComponentDirectories();
        String[] paths = new String[componentDirectories.length];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = componentDirectories[i].toString();
        }

        LOG.info("Starting DIYLC with working directory " + System.getProperty("user.dir"));
        LOG.info("Component directories: " + String.join(", ", paths));

        /*
         * Load components
         */
        ComponentRegistry.INSTANCE.init();

        setUncaughtExceptionHandler();

        warnScriptRun();

        lru = Configuration.INSTANCE.getLru();

        try {
            autoSave = new AutoSave(this);
        } catch (IOException e) {
            LOG.error("Error installng auto-same", e);
        }

        try {
            SwingUtilities.invokeAndWait(() -> {
                if (args.length > 0) {
                    openProject(Paths.get(args[0]));
                } else {
                    showTemplates();
                    createNewProject();
                }

                splashScreen.dispose();

                setDefaultMenuBar();
            });
        } catch (Exception e) {
            LOG.error("Error starting view", e);
        }
        
        // if (showWindow()) {
        // if (args.length > 0) {
        // openProject(Paths.get(args[0]));
        // } else {
        // showTemplates();
        // }
        //
        // }
        Platform.getPlatform().setPreferencesHandler((PreferencesEvent e) -> LOG.debug("Show preferences dialog"));
        Platform.getPlatform().setQuitHandler(new QuitHandler() {

            @Override
            public void handleQuit(QuitEvent event, QuitResponse response) {
                exit(response);
            }

        });
    }

    private void setDefaultMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu(MenuConstants.FILE_MENU);
        jMenuBar.add(fileMenu);
        fileMenu.add(new GenericAction("New", AppIconLoader.DocumentPlainYellow.getIcon(), Accelerators.NEW, (event) -> createNewProject()));
        fileMenu.add(new GenericAction("Open", AppIconLoader.FolderOut.getIcon(), Accelerators.OPEN, (event) -> open()));
        fileMenu.add(new JMenu(MenuConstants.FILE_OPEN_RECENT_MENU));
        fileMenu.addSeparator();
        fileMenu.add(new GenericAction("Import", AppIconLoader.ElementInto.getIcon(), Accelerators.IMPORT, (event) -> importProject()));
        fileMenu.addSeparator();
        fileMenu.add(new GenericAction("Restart", (event) -> exit(new RestartQuitResponse())));

        if (!SystemUtils.isMac()) {
            fileMenu.addSeparator();
            fileMenu.add(new GenericAction("Exit", AppIconLoader.Exit.getIcon(), (event) -> exit(new DefaultQuitResponse())));
        }

        Platform.getPlatform().setDefaultMenuBar(jMenuBar);
    }

    private void showTemplates() {
        // boolean showTemplates = Configuration.INSTANCE.getShowTemplates();
        // if (showTemplates) {
        // // TemplateDialog templateDialog = new
        // TemplateDialog(getCurrentView().getFrame(),
        // getCurrentView().getModel());
        // TemplateDialog templateDialog = new TemplateDialog(null, );
        // if (!templateDialog.getFiles().isEmpty()) {
        // templateDialog.setVisible(true);
        // }
        // }
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

    public Drawing getCurrentDrawing() {
        return currentDrawing;
    }

    public void setCurrentDrawing(Drawing drawing) {
        if (!getDrawings().contains(drawing)) {
            getDrawings().add(0, drawing);
        }

        this.currentDrawing = drawing;
    }

    public View getCurrentView() {
        return getCurrentDrawing().getView();
    }

    public DrawingModel getCurrentModel() {
        return getCurrentView().getModel();
    }

    @Override
    public void addLruPath(Path path) {
        lru.addItem(path);
        getDrawings().stream().forEach((drawing) -> drawing.getView().updateLru(lru));
    }

    @Override
    public void removeLruPath(Path path) {
        lru.removeItem(path);
        getDrawings().stream().forEach((drawing) -> drawing.getView().updateLru(lru));
    }

    public List<Drawing> getDrawings() {
        return drawings;
    }

    public Drawing createNewProject() {
        Drawing drawing = new Drawing(this);
        setCurrentDrawing(drawing);

        drawing.getView().updateLru(lru);

        return drawing;
    }

    public Drawing openProject(Path path) {
        Drawing drawing = new Drawing(this, path);
        setCurrentDrawing(drawing);

        drawing.getView().updateLru(lru);

        return drawing;
    }

    public void open() {
        LOG.info("OpenAction triggered");
        final Path path = DialogFactory.getInstance().showOpenDialog(FileFilterEnum.DIY.getFilter(), Configuration.INSTANCE.getLastPath(),
                null, FileFilterEnum.DIY.getExtensions()[0], null);

        if (path != null) {
            new Async().execute(() -> {
                LOG.debug("Opening from " + path.toAbsolutePath());
                openProject(path);
                return null;
            }, Async.onSuccess((result) -> {
                Configuration.INSTANCE.setLastPath(path.getParent().toAbsolutePath().normalize());
                addLruPath(path);
            }), Async.onError((Exception e) -> {
                getCurrentView().showMessage("Could not open file. " + e.getMessage(), "Error", ISwingUI.ERROR_MESSAGE);
            }));
        }
    }

    public void open(Path path) {
        LOG.info("OpenRecentAction triggered");
        if (path != null) {
            new Async().execute(() -> {
                LOG.debug("Opening from " + path.toAbsolutePath().normalize());
                openProject(path);
                return null;
            }, Async.onSuccess((result) -> {
                Configuration.INSTANCE.setLastPath(path.getParent().toAbsolutePath().normalize());
                addLruPath(path);
            }), Async.onError((Exception e) -> {
                removeLruPath(path);
                getCurrentView().showMessage("Could not open file. " + e.getMessage(), "Error", ISwingUI.ERROR_MESSAGE);
            }));
        }
    }

    public void importProject() {
        LOG.info("ImportAction triggered");
        Presenter presenter = new Presenter(new IView() {

            @Override
            public int showConfirmDialog(String message, String title, int optionType, int messageType) {
                return JOptionPane.showConfirmDialog(null, message, title, optionType, messageType);
            }

            @Override
            public void showMessage(String message, String title, int messageType) {
                JOptionPane.showMessageDialog(null, message, title, messageType);
            }

            @Override
            public Path promptFileSave() {
                return null;
            }

            @Override
            public boolean editProperties(List<PropertyWrapper> properties, Set<PropertyWrapper> defaultedProperties) {
                return false;
            }
        });

        final Path path = DialogFactory.getInstance().showOpenDialog(FileFilterEnum.DIY.getFilter(), Configuration.INSTANCE.getLastPath(),
                null, FileFilterEnum.DIY.getExtensions()[0], null);
        if (path != null) {
            new Async().execute(() -> {
                LOG.debug("Opening from " + path.toAbsolutePath());
                /*
                 * Load project in temp presenter
                 */
                presenter.loadProjectFromFile(path);
                /*
                 * Grab all components and paste them into the main presenter
                 */
                Drawing drawing = createNewProject();
                drawing.getModel().pasteComponents(presenter.getCurrentProject().getComponents());
                /*
                 * Cleanup components in the temp presenter, don't need them
                 * anymore
                 */
                presenter.selectAll(0);
                presenter.deleteSelectedComponents();
                return null;
            }, Async.onError((Exception e) -> {
                getCurrentView().showMessage("Could not open file. " + e.getMessage(), "Error", ISwingUI.ERROR_MESSAGE);
            }));
        }
    }

    public void exit(QuitResponse response) {
        if (getDrawings().stream().map((drawing) -> drawing.getController().close()).noneMatch((closed) -> !closed)) {
            Configuration.INSTANCE.setAbnormalExit(false);
            if (response instanceof RestartQuitResponse) {
                BootUtils.installRestarer(new Restarter(new String[0]));
            }
            response.performQuit();
        } else {
            response.cancelQuit();
        }
    }

}
