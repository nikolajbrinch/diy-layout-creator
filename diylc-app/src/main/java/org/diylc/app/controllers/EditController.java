package org.diylc.app.controllers;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.diylc.app.ComponentTransferable;
import org.diylc.app.ExpansionMode;
import org.diylc.app.view.dialogs.ButtonDialog;
import org.diylc.app.view.dialogs.DialogFactory;
import org.diylc.app.view.dialogs.PropertyEditorDialog;
import org.diylc.core.IDIYComponent;
import org.diylc.core.PropertyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface EditController extends MenuController {

    static final Logger LOG = LoggerFactory.getLogger(EditController.class);

    default void cut() {
        LOG.info("Cut triggered");
        getClipboard().setContents(new ComponentTransferable(cloneComponents(getPlugInPort().getSelectedComponents())), getClipboardOwner());
        getPlugInPort().deleteSelectedComponents();
    }

    default void copy() {
        LOG.info("Copy triggered");
        getClipboard().setContents(new ComponentTransferable(cloneComponents(getPlugInPort().getSelectedComponents())), getClipboardOwner());
    }

    @SuppressWarnings("unchecked")
    default void paste() {
        LOG.info("Paste triggered");
        try {
            List<IDIYComponent> components = (List<IDIYComponent>) getClipboard().getData(ComponentTransferable.listFlavor);
            getPlugInPort().pasteComponents(cloneComponents(components));
        } catch (Exception ex) {
            LOG.error("Coule not paste.", ex);
        }
    }

    default void delete() {
        LOG.info("Delete Selection triggered");
        getPlugInPort().deleteSelectedComponents();
    }

    default void edit() {
        LOG.info("Edit Selection triggered");
        List<PropertyWrapper> properties = getPlugInPort().getMutualSelectionProperties();
        if (properties == null || properties.isEmpty()) {
            LOG.info("Nothing to edit");
            return;
        }
        getPlugInPort().editSelection();
    }

    default void editProject() {
        LOG.info("Edit Project triggered");
        List<PropertyWrapper> properties = getPlugInPort().getProjectProperties();
        PropertyEditorDialog editor = DialogFactory.getInstance().createPropertyEditorDialog(properties, "Edit Project");
        editor.setVisible(true);
        
        if (ButtonDialog.OK.equals(editor.getSelectedButtonCaption())) {
            getPlugInPort().applyPropertiesToProject(properties);
        }
        
        /* 
         * Save default values.
         */
        for (PropertyWrapper property : editor.getDefaultedProperties()) {
            if (property.getValue() != null) {
                getPlugInPort().setProjectDefaultPropertyValue(property.getName(), property.getValue());
            }
        }
    }

    default void saveAsTemplate() {
        LOG.info("Save as template triggered");
        String templateName = JOptionPane.showInputDialog(null, "Template name:", "Save as Template", JOptionPane.PLAIN_MESSAGE);
        if (templateName != null && !templateName.trim().isEmpty()) {
            getPlugInPort().saveSelectedComponentAsTemplate(templateName);
        }
    }

    default void expandSelection(ExpansionMode expansionMode) {
        LOG.info("Expand Selection triggered: " + expansionMode);
        getPlugInPort().expandSelection(expansionMode);
    }

    default List<IDIYComponent> cloneComponents(List<IDIYComponent> components) {
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

    public Clipboard getClipboard();
    
    public ClipboardOwner getClipboardOwner();

}
