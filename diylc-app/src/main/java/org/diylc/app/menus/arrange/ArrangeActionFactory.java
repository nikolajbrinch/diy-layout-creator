package org.diylc.app.menus.arrange;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.diylc.app.dialogs.ButtonDialog;
import org.diylc.app.dialogs.DialogFactory;
import org.diylc.app.dialogs.properties.PropertyEditorDialog;
import org.diylc.app.menus.edit.ExpansionMode;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.IPlugInPort;
import org.diylc.core.PropertyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.util.Utils;

public enum ArrangeActionFactory {
    
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(ArrangeActionFactory.class);

    public GroupAction createGroupAction(IPlugInPort plugInPort) {
        return new GroupAction(plugInPort);
    }

    public UngroupAction createUngroupAction(IPlugInPort plugInPort) {
        return new UngroupAction(plugInPort);
    }

    public EditProjectAction createEditProjectAction(IPlugInPort plugInPort) {
        return new EditProjectAction(plugInPort);
    }

    public EditSelectionAction createEditSelectionAction(IPlugInPort plugInPort) {
        return new EditSelectionAction(plugInPort);
    }

    public DeleteSelectionAction createDeleteSelectionAction(IPlugInPort plugInPort) {
        return new DeleteSelectionAction(plugInPort);
    }

    public SaveAsTemplateAction createSaveAsTemplateAction(IPlugInPort plugInPort) {
        return new SaveAsTemplateAction(plugInPort);
    }

    public ExpandSelectionAction createExpandSelectionAction(IPlugInPort plugInPort, ExpansionMode expansionMode) {
        return new ExpandSelectionAction(plugInPort, expansionMode);
    }

    public RotateSelectionAction createRotateSelectionAction(IPlugInPort plugInPort, int direction) {
        return new RotateSelectionAction(plugInPort, direction);
    }

    public SendToBackAction createSendToBackAction(IPlugInPort plugInPort) {
        return new SendToBackAction(plugInPort);
    }

    public BringToFrontAction createBringToFrontAction(IPlugInPort plugInPort) {
        return new BringToFrontAction(plugInPort);
    }

    public RenumberAction createRenumberAction(IPlugInPort plugInPort, boolean xAxisFirst) {
        return new RenumberAction(plugInPort, xAxisFirst);
    }


    
    public static class GroupAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;

        public GroupAction(IPlugInPort plugInPort) {
            super();
            this.plugInPort = plugInPort;
            putValue(AbstractAction.NAME, "Group Selection");

            if (Utils.isMac()) {
                putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit()
                        .getMenuShortcutKeyMask() | ActionEvent.SHIFT_MASK));
            } else {
                putValue(AbstractAction.ACCELERATOR_KEY,
                        KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            }
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.Group.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Group Selection triggered");
            plugInPort.groupSelectedComponents();
        }
    }

    public static class UngroupAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;

        public UngroupAction(IPlugInPort plugInPort) {
            super();
            this.plugInPort = plugInPort;
            putValue(AbstractAction.NAME, "Ungroup Selection");
            if (Utils.isMac()) {
                putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, Toolkit.getDefaultToolkit()
                        .getMenuShortcutKeyMask() | ActionEvent.SHIFT_MASK));
            } else {
                putValue(AbstractAction.ACCELERATOR_KEY,
                        KeyStroke.getKeyStroke(KeyEvent.VK_U, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            }
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.Ungroup.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Ungroup Selection triggered");
            plugInPort.ungroupSelectedComponents();
        }
    }

    public static class EditProjectAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;

        public EditProjectAction(IPlugInPort plugInPort) {
            super();
            this.plugInPort = plugInPort;
            putValue(AbstractAction.NAME, "Edit Project");
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.DocumentEdit.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Edit Project triggered");
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
    


    public static class EditSelectionAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;

        public EditSelectionAction(IPlugInPort plugInPort) {
            super();
            this.plugInPort = plugInPort;
            putValue(AbstractAction.NAME, "Edit Selection");
            putValue(AbstractAction.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.EditComponent.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Edit Selection triggered");
            List<PropertyWrapper> properties = plugInPort.getMutualSelectionProperties();
            if (properties == null || properties.isEmpty()) {
                LOG.info("Nothing to edit");
                return;
            }
            plugInPort.editSelection();
        }
    }

    public static class DeleteSelectionAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;

        public DeleteSelectionAction(IPlugInPort plugInPort) {
            super();
            this.plugInPort = plugInPort;
            putValue(AbstractAction.NAME, "Delete Selection");
            putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.Delete.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Delete Selection triggered");
            plugInPort.deleteSelectedComponents();
        }
    }

    public static class SaveAsTemplateAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;

        public SaveAsTemplateAction(IPlugInPort plugInPort) {
            super();
            this.plugInPort = plugInPort;
            putValue(AbstractAction.NAME, "Save as Template");
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.BriefcaseAdd.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Save as template triggered");
            String templateName = JOptionPane.showInputDialog(null, "Template name:", "Save as Template", JOptionPane.PLAIN_MESSAGE);
            if (templateName != null && !templateName.trim().isEmpty()) {
                plugInPort.saveSelectedComponentAsTemplate(templateName);
            }
        }
    }

    public static class ExpandSelectionAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;
        private ExpansionMode expansionMode;

        public ExpandSelectionAction(IPlugInPort plugInPort, ExpansionMode expansionMode) {
            super();
            this.plugInPort = plugInPort;
            this.expansionMode = expansionMode;
            switch (expansionMode) {
            case ALL:
                putValue(AbstractAction.NAME, "All Connected");
                break;
            case IMMEDIATE:
                putValue(AbstractAction.NAME, "Immediate Only");
                break;
            case SAME_TYPE:
                putValue(AbstractAction.NAME, "Same Type Only");
                break;

            default:
                break;
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Expand Selection triggered: " + expansionMode);
            plugInPort.expandSelection(expansionMode);
        }
    }
    
    public static class RotateSelectionAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;
        private int direction;

        public RotateSelectionAction(IPlugInPort plugInPort, int direction) {
            super();
            this.plugInPort = plugInPort;
            this.direction = direction;
            if (direction > 0) {
                putValue(AbstractAction.NAME, "Rotate Clockwise");
                putValue(AbstractAction.SMALL_ICON, AppIconLoader.RotateCW.getIcon());
                putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ActionEvent.ALT_MASK));
            } else {
                putValue(AbstractAction.NAME, "Rotate Counterclockwise");
                putValue(AbstractAction.SMALL_ICON, AppIconLoader.RotateCCW.getIcon());
                putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.ALT_MASK));
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Rotate Selection triggered: " + direction);
            plugInPort.rotateSelection(direction);
        }
    }

    public static class SendToBackAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;

        public SendToBackAction(IPlugInPort plugInPort) {
            super();
            this.plugInPort = plugInPort;
            putValue(AbstractAction.NAME, "Send Backward");
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.Back.getIcon());

            if (Utils.isMac()) {
                putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_B, Toolkit.getDefaultToolkit()
                        .getMenuShortcutKeyMask() | ActionEvent.SHIFT_MASK));
            } else {
                putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, ActionEvent.ALT_MASK));
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Send to Back triggered");
            plugInPort.sendSelectionToBack();
        }
    }

    public static class BringToFrontAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;

        public BringToFrontAction(IPlugInPort plugInPort) {
            super();
            this.plugInPort = plugInPort;
            putValue(AbstractAction.NAME, "Bring Forward");
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.Front.getIcon());
            if (Utils.isMac()) {
                putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit()
                        .getMenuShortcutKeyMask() | ActionEvent.SHIFT_MASK));
            } else {
                putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_UP, ActionEvent.ALT_MASK));
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Bring to Front triggered");
            plugInPort.bringSelectionToFront();
        }
    }

    public static class RenumberAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;
        private boolean xAxisFirst;

        public RenumberAction(IPlugInPort plugInPort, boolean xAxisFirst) {
            super();
            this.plugInPort = plugInPort;
            this.xAxisFirst = xAxisFirst;
            putValue(AbstractAction.NAME, xAxisFirst ? "X Axis First" : "Y Axis First");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info(getValue(AbstractAction.NAME) + " triggered");
            plugInPort.renumberSelectedComponents(xAxisFirst);
        }
    }

//    private static List<IDIYComponent> cloneComponents(List<IDIYComponent> components) {
//        List<IDIYComponent> result = new ArrayList<IDIYComponent>(components.size());
//        for (IDIYComponent component : components) {
//            try {
//                result.add(component.clone());
//            } catch (CloneNotSupportedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return result;
//    }

}
