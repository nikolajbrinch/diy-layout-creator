package org.diylc.app.menus.file.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.diylc.app.IPlugInPort;
import org.diylc.app.dialogs.ButtonDialog;
import org.diylc.app.dialogs.DialogFactory;
import org.diylc.app.dialogs.properties.PropertyEditorDialog;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.core.PropertyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(NewAction.class);

    private static final long serialVersionUID = 1L;

    private IPlugInPort plugInPort;

    public NewAction(IPlugInPort plugInPort) {
        super();
        this.plugInPort = plugInPort;
        putValue(AbstractAction.NAME, "New");
        putValue(AbstractAction.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        putValue(AbstractAction.SMALL_ICON, AppIconLoader.DocumentPlainYellow.getIcon());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOG.info("NewAction triggered");
        if (!plugInPort.allowFileAction()) {
            return;
        }
        plugInPort.createNewProject();
        List<PropertyWrapper> properties = plugInPort.getProjectProperties();
        PropertyEditorDialog editor = DialogFactory.getInstance().createPropertyEditorDialog(properties, "Edit Project");
        editor.setVisible(true);
        if (ButtonDialog.OK.equals(editor.getSelectedButtonCaption())) {
            plugInPort.applyPropertiesToProject(properties);
        }
        // Save default values.
        for (PropertyWrapper property : editor.getDefaultedProperties()) {
            if (property.getValue() != null) {
                plugInPort.setProjectDefaultPropertyValue(property.getName(), property.getValue());
            }
        }
    }
}