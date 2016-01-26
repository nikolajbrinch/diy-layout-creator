package org.diylc.app.menus.file.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Path;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.diylc.app.IPlugInPort;
import org.diylc.app.ISwingUI;
import org.diylc.app.ITask;
import org.diylc.app.dialogs.DialogFactory;
import org.diylc.app.menus.file.FileFilterEnum;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveAsAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(SaveAsAction.class);

    private static final long serialVersionUID = 1L;

    private IPlugInPort plugInPort;

    private ISwingUI swingUI;

    public SaveAsAction(IPlugInPort plugInPort, ISwingUI swingUI) {
        super();
        this.plugInPort = plugInPort;
        this.swingUI = swingUI;
        putValue(AbstractAction.NAME, "Save As");
        putValue(AbstractAction.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | ActionEvent.SHIFT_MASK));
        putValue(AbstractAction.SMALL_ICON, AppIconLoader.DiskBlue.getIcon());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOG.info("SaveAsAction triggered");
        final Path path = DialogFactory.getInstance().showSaveDialog(FileFilterEnum.DIY.getFilter(), Configuration.INSTANCE.getLastPath(), null,
                FileFilterEnum.DIY.getExtensions()[0], null);
        if (path != null) {
            swingUI.executeBackgroundTask(new ITask<Void>() {

                @Override
                public Void doInBackground() throws Exception {
                    LOG.debug("Saving to " + path.toAbsolutePath());
                    plugInPort.saveProjectToFile(path, false);
                    return null;
                }

                @Override
                public void complete(Void result) {
                    Configuration.INSTANCE.setLastPath(path.getParent().toAbsolutePath().normalize());
                    plugInPort.addLruPath(path);
                }

                @Override
                public void failed(Exception e) {
                    swingUI.showMessage("Could not save to file. " + e.getMessage(), "Error", ISwingUI.ERROR_MESSAGE);
                }
            });
        }
    }
}