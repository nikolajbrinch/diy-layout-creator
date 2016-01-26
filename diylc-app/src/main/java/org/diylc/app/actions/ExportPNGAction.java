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

public class ExportPNGAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(ExportPNGAction.class);

    private static final long serialVersionUID = 1L;

    private IDrawingProvider drawingProvider;
    private ISwingUI swingUI;

    public ExportPNGAction(IDrawingProvider drawingProvider, ISwingUI swingUI) {
        super();
        this.drawingProvider = drawingProvider;
        this.swingUI = swingUI;
        putValue(AbstractAction.NAME, "Export to PNG");
        putValue(AbstractAction.SMALL_ICON, AppIconLoader.Image.getIcon());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOG.info("ExportPNGAction triggered");
        final Path path = DialogFactory.getInstance().showSaveDialog(FileFilterEnum.PNG.getFilter(), Configuration.INSTANCE.getLastPath(), null,
                FileFilterEnum.PNG.getExtensions()[0], null);
        if (path != null) {
            swingUI.executeBackgroundTask(new ITask<Void>() {

                @Override
                public Void doInBackground() throws Exception {
                    LOG.debug("Exporting to " + path.toAbsolutePath());
                    DrawingExporter.getInstance().exportPNG(ExportPNGAction.this.drawingProvider, path);
                    return null;
                }

                @Override
                public void complete(Void result) {
                }

                @Override
                public void failed(Exception e) {
                    swingUI.showMessage("Could not export to PNG. " + e.getMessage(), "Error", ISwingUI.ERROR_MESSAGE);
                }
            });
        }
    }
}
