package org.diylc.app.menus.file.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.diylc.app.AppIconLoader;
import org.diylc.app.IPlugInPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExitAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(ExitAction.class);

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