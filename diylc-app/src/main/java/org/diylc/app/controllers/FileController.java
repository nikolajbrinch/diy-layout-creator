package org.diylc.app.controllers;

import java.nio.file.Path;

import org.diylc.app.FileFilterEnum;
import org.diylc.app.model.Model;
import org.diylc.app.utils.async.Async;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.ISwingUI;
import org.diylc.app.view.View;
import org.diylc.app.view.canvas.IDrawingProvider;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileController extends AbstractController implements ExportController, PrintController {

    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

    private final IDrawingProvider drawingProvider;

    public FileController(ApplicationController applicationController, View view, Model model, DrawingController controller, IPlugInPort plugInPort,
            IDrawingProvider drawingProvider) {
        super(applicationController, view, model, controller, plugInPort);
        this.drawingProvider = drawingProvider;
    }

    public IDrawingProvider getDrawingProvider() {
        return drawingProvider;
    }

    public void newProject() {
        getApplicationController().createNewProject();
    }

    // public void newProject() {
    // LOG.info("NewAction triggered");
    //
    // if (!getPlugInPort().allowFileAction()) {
    // return;
    // }
    //
    // getPlugInPort().createNewProject();
    // List<PropertyWrapper> properties =
    // getPlugInPort().getProjectProperties();
    // PropertyEditorDialog editor =
    // DialogFactory.getInstance().createPropertyEditorDialog(properties,
    // "Edit Project");
    // editor.setVisible(true);
    // if (ButtonDialog.OK.equals(editor.getSelectedButtonCaption())) {
    // getPlugInPort().applyPropertiesToProject(properties);
    // }
    // // Save default values.
    // for (PropertyWrapper property : editor.getDefaultedProperties()) {
    // if (property.getValue() != null) {
    // getPlugInPort().setProjectDefaultPropertyValue(property.getName(),
    // property.getValue());
    // }
    // }
    // }

    public void save() {
        LOG.info("SaveAction triggered");
        if (getModel().getCurrentFile() == null) {
            final Path path = getView().showSaveDialog(FileFilterEnum.DIY.getFilter(),
                    Configuration.INSTANCE.getLastPath(), null, FileFilterEnum.DIY.getExtensions()[0], null);
            if (path != null) {
                new Async(() -> getView().block(), () -> getView().unblock()).execute(() -> {
                    LOG.debug("Saving to " + path.toAbsolutePath());
                    getModel().saveProjectToFile(path, false);
                    return null;
                }, Async.onSuccess((result) -> {
                    Configuration.INSTANCE.setLastPath(path.getParent().toAbsolutePath().normalize());
                    getApplicationController().addLruPath(path);
                }), Async.onError((Exception e) -> {
                    getView().showMessage("Could not save to file. " + e.getMessage(), "Error", ISwingUI.ERROR_MESSAGE);
                }));
            }
        } else {
            new Async(() -> getView().block(), () -> getView().unblock()).execute(() -> {
                LOG.debug("Saving to " + getModel().getCurrentFile().toAbsolutePath());
                getModel().saveProjectToFile(getModel().getCurrentFile(), false);
                return null;
            }, Async.onError((Exception e) -> {
                getView().showMessage("Could not save to file. " + e.getMessage(), "Error", ISwingUI.ERROR_MESSAGE);
            }));
        }
    }

    public void saveAs() {
        LOG.info("SaveAsAction triggered");
        final Path path = getView().showSaveDialog(FileFilterEnum.DIY.getFilter(), Configuration.INSTANCE.getLastPath(),
                null, FileFilterEnum.DIY.getExtensions()[0], null);
        if (path != null) {
            new Async(() -> getView().block(), () -> getView().unblock()).execute(() -> {
                LOG.debug("Saving to " + path.toAbsolutePath());
                getModel().saveProjectToFile(path, false);
                return null;
            }, Async.onSuccess((result) -> {
                Configuration.INSTANCE.setLastPath(path.getParent().toAbsolutePath().normalize());
                getApplicationController().addLruPath(path);
            }), Async.onError((Exception e) -> {
                getView().showMessage("Could not save to file. " + e.getMessage(), "Error", ISwingUI.ERROR_MESSAGE);
            }));
        }
    }

    public void close() {
        getDrawing().getController().close();
    }
}
