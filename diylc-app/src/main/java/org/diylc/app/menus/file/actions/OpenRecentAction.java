package org.diylc.app.menus.file.actions;

import java.awt.event.ActionEvent;
import java.nio.file.Path;

import javax.swing.AbstractAction;

import org.diylc.app.IPlugInPort;
import org.diylc.app.ISwingUI;
import org.diylc.app.ITask;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public  class OpenRecentAction extends AbstractAction {
    
    private static final Logger LOG = LoggerFactory.getLogger(OpenRecentAction.class);
    
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
        
        Path lastPath = Configuration.INSTANCE.getLastPath();

        if (lastPath != null) {
            try {
                lastPath = lastPath.toAbsolutePath().normalize();
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
                    plugInPort.removeLruPath(path);
                    swingUI.showMessage(
                            "Could not open file. " + e.getMessage(),
                            "Error", ISwingUI.ERROR_MESSAGE);
                }
            });
        }
    }
}