package org.diylc.app.menus.file;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.diylc.app.AppIconLoader;
import org.diylc.app.IPlugInPort;
import org.diylc.app.ISwingUI;
import org.diylc.app.ITask;
import org.diylc.app.IView;
import org.diylc.app.dialogs.ButtonDialog;
import org.diylc.app.dialogs.DialogFactory;
import org.diylc.app.dialogs.properties.PropertyEditorDialog;
import org.diylc.app.menus.tools.BomDialog;
import org.diylc.app.view.Presenter;
import org.diylc.app.view.canvas.IDrawingProvider;
import org.diylc.core.BomMaker;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.config.Configuration;
import org.diylc.core.BomEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum FileActionFactory {

    INSTANCE;

    private static final Logger LOG = LoggerFactory
            .getLogger(FileActionFactory.class);

    public NewAction createNewAction(IPlugInPort plugInPort) {
        return new NewAction(plugInPort);
    }

    public OpenAction createOpenAction(IPlugInPort plugInPort, ISwingUI swingUI) {
        return new OpenAction(plugInPort, swingUI);
    }

    public OpenRecentAction createOpenRecentAction(IPlugInPort plugInPort,
            ISwingUI swingUI, Path path) {
        return new OpenRecentAction(plugInPort, swingUI, path);
    }

    public ImportAction createImportAction(IPlugInPort plugInPort,
            ISwingUI swingUI) {
        return new ImportAction(plugInPort, swingUI);
    }

    public SaveAction createSaveAction(IPlugInPort plugInPort, ISwingUI swingUI) {
        return new SaveAction(plugInPort, swingUI);
    }

    public SaveAsAction createSaveAsAction(IPlugInPort plugInPort,
            ISwingUI swingUI) {
        return new SaveAsAction(plugInPort, swingUI);
    }

    public CreateBomAction createBomAction(IPlugInPort plugInPort) {
        return new CreateBomAction(plugInPort);
    }

    public ExportPDFAction createExportPDFAction(
            IDrawingProvider drawingProvider, ISwingUI swingUI) {
        return new ExportPDFAction(drawingProvider, swingUI);
    }

    public ExportPNGAction createExportPNGAction(
            IDrawingProvider drawingProvider, ISwingUI swingUI) {
        return new ExportPNGAction(drawingProvider, swingUI);
    }

    public PrintAction createPrintAction(IDrawingProvider drawingProvider) {
        return new PrintAction(drawingProvider);
    }

    public ExitAction createExitAction(IPlugInPort plugInPort) {
        return new ExitAction(plugInPort);
    }
    
    // File menu actions.

    public static class NewAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;

        public NewAction(IPlugInPort plugInPort) {
            super();
            this.plugInPort = plugInPort;
            putValue(AbstractAction.NAME, "New");
            putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_N, Toolkit.getDefaultToolkit()
                            .getMenuShortcutKeyMask()));
            putValue(AbstractAction.SMALL_ICON,
                    AppIconLoader.DocumentPlainYellow.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("NewAction triggered");
            if (!plugInPort.allowFileAction()) {
                return;
            }
            plugInPort.createNewProject();
            List<PropertyWrapper> properties = plugInPort
                    .getProjectProperties();
            PropertyEditorDialog editor = DialogFactory.getInstance()
                    .createPropertyEditorDialog(properties, "Edit Project");
            editor.setVisible(true);
            if (ButtonDialog.OK.equals(editor.getSelectedButtonCaption())) {
                plugInPort.applyPropertiesToProject(properties);
            }
            // Save default values.
            for (PropertyWrapper property : editor.getDefaultedProperties()) {
                if (property.getValue() != null) {
                    plugInPort.setProjectDefaultPropertyValue(
                            property.getName(), property.getValue());
                }
            }
        }
    }

    public static class OpenAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;
        private ISwingUI swingUI;

        public OpenAction(IPlugInPort plugInPort, ISwingUI swingUI) {
            super();
            this.plugInPort = plugInPort;
            this.swingUI = swingUI;
            putValue(AbstractAction.NAME, "Open");
            putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_O, Toolkit.getDefaultToolkit()
                            .getMenuShortcutKeyMask()));
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.FolderOut.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("OpenAction triggered");
            if (!plugInPort.allowFileAction()) {
                return;
            }
            final File file = DialogFactory.getInstance().showOpenDialog(
                    FileFilterEnum.DIY.getFilter(), null,
                    FileFilterEnum.DIY.getExtensions()[0], null);
            if (file != null) {
                swingUI.executeBackgroundTask(new ITask<Void>() {

                    @Override
                    public Void doInBackground() throws Exception {
                        LOG.debug("Opening from " + file.getAbsolutePath());
                        plugInPort.loadProjectFromFile(file);
                        return null;
                    }

                    @Override
                    public void complete(Void result) {
                        plugInPort.addLruPath(file.toPath());
                    }

                    @Override
                    public void failed(Exception e) {
                        swingUI.showMessage(
                                "Could not open file. " + e.getMessage(),
                                "Error", ISwingUI.ERROR_MESSAGE);
                    }
                });
            }
        }
    }

    public static class OpenRecentAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;
        private ISwingUI swingUI;
        private Path path;

        public OpenRecentAction(IPlugInPort plugInPort, ISwingUI swingUI,
                Path path) {
            super();
            this.plugInPort = plugInPort;
            this.swingUI = swingUI;
            this.path = path;
            putValue(AbstractAction.NAME, createDisplayName(path));
        }

        private String createDisplayName(Path path) {
            String displayName = path.toString();
            
            String lastPathName = Configuration.INSTANCE.getLastPath();

            if (lastPathName != null) {
                try {
                    Path lastPath = Paths.get(lastPathName).toAbsolutePath().normalize();
                    Path displayPath = lastPath.toAbsolutePath().normalize().relativize(path);
                    
                    displayName = displayPath.toString();
                } catch (IllegalArgumentException e) {
                    /*
                     * Ignore - a relative path cannot be calculated.
                     */
                }
            }

            return displayName;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("OpenRecentAction triggered");
            if (!plugInPort.allowFileAction()) {
                return;
            }
            if (path != null) {
                swingUI.executeBackgroundTask(new ITask<Void>() {

                    @Override
                    public Void doInBackground() throws Exception {
                        LOG.debug("Opening from " + path.toAbsolutePath().normalize());
                        plugInPort.loadProjectFromFile(path.toFile());
                        return null;
                    }

                    @Override
                    public void complete(Void result) {
                        Configuration.INSTANCE.setLastPath(path.getParent().toAbsolutePath().normalize().toString());
                        plugInPort.addLruPath(path);
                    }

                    @Override
                    public void failed(Exception e) {
                        swingUI.showMessage(
                                "Could not open file. " + e.getMessage(),
                                "Error", ISwingUI.ERROR_MESSAGE);
                    }
                });
            }
        }
    }
    

    public static class ImportAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;
        private ISwingUI swingUI;
        private Presenter presenter;

        public ImportAction(IPlugInPort plugInPort, ISwingUI swingUI) {
            super();
            this.plugInPort = plugInPort;
            this.swingUI = swingUI;
            this.presenter = new Presenter(new IView() {

                @Override
                public int showConfirmDialog(String message, String title,
                        int optionType, int messageType) {
                    return JOptionPane.showConfirmDialog(null, message, title,
                            optionType, messageType);
                }

                @Override
                public void showMessage(String message, String title,
                        int messageType) {
                    JOptionPane.showMessageDialog(null, message, title,
                            messageType);
                }

                @Override
                public File promptFileSave() {
                    return null;
                }

                @Override
                public boolean editProperties(List<PropertyWrapper> properties,
                        Set<PropertyWrapper> defaultedProperties) {
                    return false;
                }
            });
            putValue(AbstractAction.NAME, "Import");
            putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_I, Toolkit.getDefaultToolkit()
                            .getMenuShortcutKeyMask()));
            putValue(AbstractAction.SMALL_ICON,
                    AppIconLoader.ElementInto.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("ImportAction triggered");

            final File file = DialogFactory.getInstance().showOpenDialog(
                    FileFilterEnum.DIY.getFilter(), null,
                    FileFilterEnum.DIY.getExtensions()[0], null);
            if (file != null) {
                swingUI.executeBackgroundTask(new ITask<Void>() {

                    @Override
                    public Void doInBackground() throws Exception {
                        LOG.debug("Opening from " + file.getAbsolutePath());
                        // Load project in temp presenter
                        presenter.loadProjectFromFile(file);
                        // Grab all components and paste them into the main
                        // presenter
                        plugInPort.pasteComponents(presenter
                                .getCurrentProject().getComponents());
                        // Cleanup components in the temp presenter, don't need
                        // them anymore
                        presenter.selectAll(0);
                        presenter.deleteSelectedComponents();
                        return null;
                    }

                    @Override
                    public void complete(Void result) {
                    }

                    @Override
                    public void failed(Exception e) {
                        swingUI.showMessage(
                                "Could not open file. " + e.getMessage(),
                                "Error", ISwingUI.ERROR_MESSAGE);
                    }
                });
            }
        }
    }
    
    public static class SaveAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;
        private ISwingUI swingUI;

        public SaveAction(IPlugInPort plugInPort, ISwingUI swingUI) {
            super();
            this.plugInPort = plugInPort;
            this.swingUI = swingUI;
            putValue(AbstractAction.NAME, "Save");
            putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_S, Toolkit.getDefaultToolkit()
                            .getMenuShortcutKeyMask()));
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.DiskBlue.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("SaveAction triggered");
            if (plugInPort.getCurrentFile() == null) {
                final File file = DialogFactory.getInstance().showSaveDialog(
                        FileFilterEnum.DIY.getFilter(), null,
                        FileFilterEnum.DIY.getExtensions()[0], null);
                if (file != null) {
                    swingUI.executeBackgroundTask(new ITask<Void>() {

                        @Override
                        public Void doInBackground() throws Exception {
                            LOG.debug("Saving to " + file.getAbsolutePath());
                            plugInPort.saveProjectToFile(file, false);
                            return null;
                        }

                        @Override
                        public void complete(Void result) {
                            plugInPort.addLruPath(file.toPath());
                        }

                        @Override
                        public void failed(Exception e) {
                            swingUI.showMessage(
                                    "Could not save to file. " + e.getMessage(),
                                    "Error", ISwingUI.ERROR_MESSAGE);
                        }
                    });
                }
            } else {
                swingUI.executeBackgroundTask(new ITask<Void>() {

                    @Override
                    public Void doInBackground() throws Exception {
                        LOG.debug("Saving to "
                                + plugInPort.getCurrentFile().getAbsolutePath());
                        plugInPort.saveProjectToFile(plugInPort.getCurrentFile(), false);
                        return null;
                    }

                    @Override
                    public void complete(Void result) {
                    }

                    @Override
                    public void failed(Exception e) {
                        swingUI.showMessage(
                                "Could not save to file. " + e.getMessage(),
                                "Error", ISwingUI.ERROR_MESSAGE);
                    }
                });
            }
        }
    }

    public static class SaveAsAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;
        private ISwingUI swingUI;

        public SaveAsAction(IPlugInPort plugInPort, ISwingUI swingUI) {
            super();
            this.plugInPort = plugInPort;
            this.swingUI = swingUI;
            putValue(AbstractAction.NAME, "Save As");
            putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_S, Toolkit.getDefaultToolkit()
                            .getMenuShortcutKeyMask() | ActionEvent.SHIFT_MASK));
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.DiskBlue.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("SaveAsAction triggered");
            final File file = DialogFactory.getInstance().showSaveDialog(
                    FileFilterEnum.DIY.getFilter(), null,
                    FileFilterEnum.DIY.getExtensions()[0], null);
            if (file != null) {
                swingUI.executeBackgroundTask(new ITask<Void>() {

                    @Override
                    public Void doInBackground() throws Exception {
                        LOG.debug("Saving to " + file.getAbsolutePath());
                        plugInPort.saveProjectToFile(file, false);
                        return null;
                    }

                    @Override
                    public void complete(Void result) {
                        plugInPort.addLruPath(file.toPath());
                    }

                    @Override
                    public void failed(Exception e) {
                        swingUI.showMessage(
                                "Could not save to file. " + e.getMessage(),
                                "Error", ISwingUI.ERROR_MESSAGE);
                    }
                });
            }
        }
    }
    




    public static class CreateBomAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;

        public CreateBomAction(IPlugInPort plugInPort) {
            super();
            this.plugInPort = plugInPort;
            putValue(AbstractAction.NAME, "Create B.O.M.");
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.BOM.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("CreateBomAction triggered");
            List<BomEntry> bom = BomMaker.getInstance()
                    .createBom(plugInPort.getCurrentProject().getComponents());
            BomDialog dialog = DialogFactory.getInstance().createBomDialog(bom);
            dialog.setVisible(true);
        }
    }

    public static class ExportPDFAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IDrawingProvider drawingProvider;
        private ISwingUI swingUI;

        public ExportPDFAction(IDrawingProvider drawingProvider,
                ISwingUI swingUI) {
            super();
            this.drawingProvider = drawingProvider;
            this.swingUI = swingUI;
            putValue(AbstractAction.NAME, "Export to PDF");
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.PDF.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("ExportPDFAction triggered");
            final File file = DialogFactory.getInstance().showSaveDialog(
                    FileFilterEnum.PDF.getFilter(), null,
                    FileFilterEnum.PDF.getExtensions()[0], null);
            if (file != null) {
                swingUI.executeBackgroundTask(new ITask<Void>() {

                    @Override
                    public Void doInBackground() throws Exception {
                        LOG.debug("Exporting to " + file.getAbsolutePath());
                        DrawingExporter.getInstance().exportPDF(
                                ExportPDFAction.this.drawingProvider, file);
                        return null;
                    }

                    @Override
                    public void complete(Void result) {
                    }

                    @Override
                    public void failed(Exception e) {
                        swingUI.showMessage(
                                "Could not export to PDF. " + e.getMessage(),
                                "Error", ISwingUI.ERROR_MESSAGE);
                    }
                });
            }
        }
    }

    public static class ExportPNGAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IDrawingProvider drawingProvider;
        private ISwingUI swingUI;

        public ExportPNGAction(IDrawingProvider drawingProvider,
                ISwingUI swingUI) {
            super();
            this.drawingProvider = drawingProvider;
            this.swingUI = swingUI;
            putValue(AbstractAction.NAME, "Export to PNG");
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.Image.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("ExportPNGAction triggered");
            final File file = DialogFactory.getInstance().showSaveDialog(
                    FileFilterEnum.PNG.getFilter(), null,
                    FileFilterEnum.PNG.getExtensions()[0], null);
            if (file != null) {
                swingUI.executeBackgroundTask(new ITask<Void>() {

                    @Override
                    public Void doInBackground() throws Exception {
                        LOG.debug("Exporting to " + file.getAbsolutePath());
                        DrawingExporter.getInstance().exportPNG(
                                ExportPNGAction.this.drawingProvider, file);
                        return null;
                    }

                    @Override
                    public void complete(Void result) {
                    }

                    @Override
                    public void failed(Exception e) {
                        swingUI.showMessage(
                                "Could not export to PNG. " + e.getMessage(),
                                "Error", ISwingUI.ERROR_MESSAGE);
                    }
                });
            }
        }
    }

    public static class PrintAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IDrawingProvider drawingProvider;

        public PrintAction(IDrawingProvider drawingProvider) {
            super();
            this.drawingProvider = drawingProvider;
            putValue(AbstractAction.NAME, "Print...");
            putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_P, Toolkit.getDefaultToolkit()
                            .getMenuShortcutKeyMask()));
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.Print.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("PrintAction triggered");
            try {
                DrawingExporter.getInstance().print(this.drawingProvider);
            } catch (PrinterException e1) {
                LOG.warn("Error printing", e1);
            }
        }
    }

    public static class ExitAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;

        public ExitAction(IPlugInPort plugInPort) {
            super();
            this.plugInPort = plugInPort;
            putValue(AbstractAction.NAME, "Exit");
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.Exit.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("ExitAction triggered");
            if (plugInPort.allowFileAction()) {
                System.exit(0);
            }
        }
    }

}

