package org.diylc.app.menus.edit;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.diylc.app.AppIconLoader;
import org.diylc.app.ExpansionMode;
import org.diylc.app.IPlugInPort;
import org.diylc.app.dialogs.ButtonDialog;
import org.diylc.app.dialogs.DialogFactory;
import org.diylc.app.dialogs.properties.PropertyEditorDialog;
import org.diylc.core.IDIYComponent;
import org.diylc.core.PropertyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum EditActionFactory {
    
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(EditActionFactory.class);

    public CutAction createCutAction(IPlugInPort plugInPort, Clipboard clipboard, ClipboardOwner clipboardOwner) {
        return new CutAction(plugInPort, clipboard, clipboardOwner);
    }

    public CopyAction createCopyAction(IPlugInPort plugInPort, Clipboard clipboard, ClipboardOwner clipboardOwner) {
        return new CopyAction(plugInPort, clipboard, clipboardOwner);
    }

    public PasteAction createPasteAction(IPlugInPort plugInPort, Clipboard clipboard) {
        return new PasteAction(plugInPort, clipboard);
    }

    public SelectAllAction createSelectAllAction(IPlugInPort plugInPort) {
        return new SelectAllAction(plugInPort);
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

    public RenumberAction createRenumberAction(IPlugInPort plugInPort, boolean xAxisFirst) {
        return new RenumberAction(plugInPort, xAxisFirst);
    }

    public static class CutAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;
        private Clipboard clipboard;
        private ClipboardOwner clipboardOwner;

        public CutAction(IPlugInPort plugInPort, Clipboard clipboard, ClipboardOwner clipboardOwner) {
            super();
            this.plugInPort = plugInPort;
            this.clipboard = clipboard;
            this.clipboardOwner = clipboardOwner;
            putValue(AbstractAction.NAME, "Cut");
            putValue(AbstractAction.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.Cut.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Cut triggered");
            clipboard.setContents(new ComponentTransferable(cloneComponents(plugInPort.getSelectedComponents())), clipboardOwner);
            plugInPort.deleteSelectedComponents();
        }
    }

    public static class CopyAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;
        private Clipboard clipboard;
        private ClipboardOwner clipboardOwner;

        public CopyAction(IPlugInPort plugInPort, Clipboard clipboard, ClipboardOwner clipboardOwner) {
            super();
            this.plugInPort = plugInPort;
            this.clipboard = clipboard;
            this.clipboardOwner = clipboardOwner;
            putValue(AbstractAction.NAME, "Copy");
            putValue(AbstractAction.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.Copy.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Copy triggered");
            clipboard.setContents(new ComponentTransferable(cloneComponents(plugInPort.getSelectedComponents())), clipboardOwner);
        }
    }

    public static class PasteAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;
        private Clipboard clipboard;

        public PasteAction(IPlugInPort plugInPort, Clipboard clipboard) {
            super();
            this.plugInPort = plugInPort;
            this.clipboard = clipboard;
            putValue(AbstractAction.NAME, "Paste");
            putValue(AbstractAction.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.Paste.getIcon());
        }

        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Paste triggered");
            try {
                List<IDIYComponent> components = (List<IDIYComponent>) clipboard.getData(ComponentTransferable.listFlavor);
                plugInPort.pasteComponents(cloneComponents(components));
            } catch (Exception ex) {
                LOG.error("Coule not paste.", ex);
            }
        }
    }
    
    public static class SelectAllAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private IPlugInPort plugInPort;

        public SelectAllAction(IPlugInPort plugInPort) {
            super();
            this.plugInPort = plugInPort;
            putValue(AbstractAction.NAME, "Select All");
            putValue(AbstractAction.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            putValue(AbstractAction.SMALL_ICON, AppIconLoader.Selection.getIcon());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Select All triggered");
            plugInPort.selectAll(0);
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

    private static List<IDIYComponent> cloneComponents(List<IDIYComponent> components) {
        List<IDIYComponent> result = new ArrayList<IDIYComponent>(components.size());
        for (IDIYComponent component : components) {
            try {
                result.add(component.clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

}
