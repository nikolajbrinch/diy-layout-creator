package org.diylc.app.menus.file;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Path;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.diylc.app.ITask;
import org.diylc.app.dialogs.DialogFactory;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.ISwingUI;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(OpenAction.class);
    
    private static final long serialVersionUID = 1L;

    private IPlugInPort plugInPort;

    private ISwingUI swingUI;

    public OpenAction(IPlugInPort plugInPort, ISwingUI swingUI) {
        super();
        this.plugInPort = plugInPort;
        this.swingUI = swingUI;
        putValue(AbstractAction.NAME, "Open");
        putValue(AbstractAction.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        putValue(AbstractAction.SMALL_ICON, AppIconLoader.FolderOut.getIcon());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOG.info("OpenAction triggered");
        if (!plugInPort.allowFileAction()) {
            return;
        }
        final Path path = DialogFactory.getInstance().showOpenDialog(FileFilterEnum.DIY.getFilter(), 
                Configuration.INSTANCE.getLastPath(), null,
                FileFilterEnum.DIY.getExtensions()[0], null);
        if (path != null) {
            swingUI.executeBackgroundTask(new ITask<Void>() {

                @Override
                public Void doInBackground() throws Exception {
                    LOG.debug("Opening from " + path.toAbsolutePath());
                    plugInPort.loadProjectFromFile(path);
                    return null;
                }

                @Override
                public void complete(Void result) {
                    Configuration.INSTANCE.setLastPath(path.getParent().toAbsolutePath().normalize());
                    plugInPort.addLruPath(path);
                }

                @Override
                public void failed(Exception e) {
                    swingUI.showMessage("Could not open file. " + e.getMessage(), "Error", ISwingUI.ERROR_MESSAGE);
                }
            });
        }
    }
}
