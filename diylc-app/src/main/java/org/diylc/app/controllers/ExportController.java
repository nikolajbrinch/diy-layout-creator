package org.diylc.app.controllers;

import java.nio.file.Path;

import org.diylc.app.FileFilterEnum;
import org.diylc.app.utils.async.Async;
import org.diylc.app.view.DrawingExporter;
import org.diylc.app.view.ISwingUI;
import org.diylc.app.view.canvas.IDrawingProvider;
import org.diylc.app.view.dialogs.DialogFactory;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ExportController extends MenuController {

    static final Logger LOG = LoggerFactory.getLogger(ExportController.class);

    public IDrawingProvider getDrawingProvider();

    default void exportPdf() {
        LOG.info("ExportPDFAction triggered");
        final Path path = DialogFactory.getInstance().showSaveDialog(FileFilterEnum.PDF.getFilter(), Configuration.INSTANCE.getLastPath(),
                null, FileFilterEnum.PDF.getExtensions()[0], null);
        if (path != null) {
            new Async(() -> getView().block(), () -> getView().unblock()).execute(() -> {
                LOG.debug("Exporting to " + path.toAbsolutePath());
                DrawingExporter.getInstance().exportPDF(getDrawingProvider(), path);
                return null;
            }, Async.onError((Exception e) -> {
                getView().showMessage("Could not export to PDF. " + e.getMessage(), "Error", ISwingUI.ERROR_MESSAGE);
            }));
        }
    }

    default void exportPng() {
        LOG.info("ExportPNGAction triggered");
        final Path path = DialogFactory.getInstance().showSaveDialog(FileFilterEnum.PNG.getFilter(), Configuration.INSTANCE.getLastPath(),
                null, FileFilterEnum.PNG.getExtensions()[0], null);
        if (path != null) {
            new Async(() -> getView().block(), () -> getView().unblock()).execute(() -> {
                LOG.debug("Exporting to " + path.toAbsolutePath());
                DrawingExporter.getInstance().exportPNG(getDrawingProvider(), path);
                return null;
            }, Async.onError((Exception e) -> {
                getView().showMessage("Could not export to PNG. " + e.getMessage(), "Error", ISwingUI.ERROR_MESSAGE);
            }));
        }
    }

}
