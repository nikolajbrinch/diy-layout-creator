package org.diylc.app.controllers;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.diylc.app.FileFilterEnum;
import org.diylc.app.model.Model;
import org.diylc.app.utils.async.Async;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.ISwingUI;
import org.diylc.app.view.View;
import org.diylc.app.view.canvas.IDrawingProvider;
import org.diylc.app.view.dialogs.DialogFactory;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileController extends AbstractController
    implements ExportController, PrintController {

  private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

  private final IDrawingProvider drawingProvider;

  public FileController(ApplicationController applicationController, View view, Model model,
      IPlugInPort plugInPort,
      IDrawingProvider drawingProvider) {
    super(applicationController, view, model, plugInPort);
    this.drawingProvider = drawingProvider;
  }

  public IDrawingProvider getDrawingProvider() {
    return drawingProvider;
  }

  public void save() {
    LOG.info("SaveAction triggered");
    if (!getPlugInPort().isSaved()) {
      saveAs();
    } else {
      new Async(() -> getView().block(), () -> getView().unblock()).execute(() -> {
        return doSave(getPlugInPort().getCurrentFile());
      }, Async.onError((Exception e) -> {
        getView().showMessage("Could not save to file. " + e.getMessage(), "Error",
            ISwingUI.ERROR_MESSAGE);
      }));
    }
  }

  public void saveAs() {
    LOG.info("SaveAsAction triggered");
    Path initialPath = getPlugInPort().getCurrentFile().toAbsolutePath();
    String initialFilename = initialPath.toString();

    if (!initialFilename.endsWith(FileFilterEnum.DIY.getExtensions()[0])) {
      initialPath = Paths.get(initialFilename + "." + FileFilterEnum.DIY.getExtensions()[0]);
    }

    final Path path = DialogFactory.getInstance().showSaveDialog(FileFilterEnum.DIY.getFilter(),
        Configuration.INSTANCE.getLastPath(), initialPath,
        FileFilterEnum.DIY.getExtensions()[0], null);

    if (path != null) {
      new Async(() -> getView().block(), () -> getView().unblock()).execute(() -> {
        return doSave(path);
      }, Async.onSuccess((result) -> {
        updateLastPathAndLru(path);
      }), Async.onError((Exception e) -> {
        getView().showMessage("Could not save to file. " + e.getMessage(), "Error",
            ISwingUI.ERROR_MESSAGE);
      }));
    }
  }

  private Object doSave(Path path) {
    LOG.debug("Saving to " + path.toAbsolutePath());
    getPlugInPort().saveProjectToFile(path, false);
    return null;
  }

  private void updateLastPathAndLru(Path savePath) {
    Configuration.INSTANCE.setLastPath(savePath.getParent().toAbsolutePath().normalize());
    getApplicationController().addLruPath(savePath);
  }
}
