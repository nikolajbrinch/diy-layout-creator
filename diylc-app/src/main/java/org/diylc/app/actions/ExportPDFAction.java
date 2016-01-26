package org.diylc.app.actions;

import java.awt.event.ActionEvent;
import java.nio.file.Path;

import javax.swing.AbstractAction;

import org.diylc.app.ISwingUI;
import org.diylc.app.ITask;
import org.diylc.app.dialogs.DialogFactory;
import org.diylc.app.menus.file.DrawingExporter;
import org.diylc.app.menus.file.FileFilterEnum;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.canvas.IDrawingProvider;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportPDFAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(ExportPDFAction.class);

    private static final long serialVersionUID = 1L;

    private IDrawingProvider drawingProvider;
    private ISwingUI swingUI;

    public ExportPDFAction(IDrawingProvider drawingProvider, ISwingUI swingUI) {
        super();
        this.drawingProvider = drawingProvider;
        this.swingUI = swingUI;
        putValue(AbstractAction.NAME, "Export to PDF");
        putValue(AbstractAction.SMALL_ICON, AppIconLoader.PDF.getIcon());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOG.info("ExportPDFAction triggered");
        final Path path = DialogFactory.getInstance().showSaveDialog(FileFilterEnum.PDF.getFilter(), Configuration.INSTANCE.getLastPath(), null,
                FileFilterEnum.PDF.getExtensions()[0], null);
        if (path != null) {
            swingUI.executeBackgroundTask(new ITask<Void>() {

                @Override
                public Void doInBackground() throws Exception {
                    LOG.debug("Exporting to " + path.toAbsolutePath());
                    DrawingExporter.getInstance().exportPDF(ExportPDFAction.this.drawingProvider, path);
                    return null;
                }

                @Override
                public void complete(Void result) {
                }

                @Override
                public void failed(Exception e) {
                    swingUI.showMessage("Could not export to PDF. " + e.getMessage(), "Error", ISwingUI.ERROR_MESSAGE);
                }
            });
        }
    }
}