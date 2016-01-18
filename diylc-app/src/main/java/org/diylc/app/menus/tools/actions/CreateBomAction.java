package org.diylc.app.menus.tools.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import org.diylc.app.AppIconLoader;
import org.diylc.app.IPlugInPort;
import org.diylc.app.dialogs.DialogFactory;
import org.diylc.app.menus.tools.BomDialog;
import org.diylc.core.BomEntry;
import org.diylc.core.BomMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateBomAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(CreateBomAction.class);

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
        List<BomEntry> bom = BomMaker.getInstance().createBom(plugInPort.getCurrentProject().getComponents());
        BomDialog dialog = DialogFactory.getInstance().createBomDialog(bom);
        dialog.setVisible(true);
    }
}
