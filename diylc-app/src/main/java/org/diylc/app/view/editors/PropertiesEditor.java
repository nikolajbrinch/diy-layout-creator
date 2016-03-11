package org.diylc.app.view.editors;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.diylc.core.PropertyWrapper;
import org.diylc.specifications.PropertyEditor;
import org.diylc.specifications.SpecificationProperty;

public class PropertiesEditor extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private static final String DEFAULT_BOX_TOOLTIP = "<html>If this box is checked application will use the current value as a<br>default when creating new components of the same type</html>";
    
    public final boolean showDefaultBoxes;

    private final List<PropertyWrapper> properties;

    private final Set<PropertyWrapper> defaultedProperties;

    private Component componentToFocus = null;
    
    private Map<String, JComponent> components = new HashMap<>();

    public PropertiesEditor(List<PropertyWrapper> properties) {
        this(properties, null);
    }
    
    public PropertiesEditor(List<PropertyWrapper> properties, Set<PropertyWrapper> defaultedProperties) {
        this.properties = properties;
        this.defaultedProperties = defaultedProperties;
        this.showDefaultBoxes = defaultedProperties != null;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        buildEditors();
    }
    
    public void addKeyListener(KeyListener keyListener) {
        for (JComponent component : components.values()) {
            component.addKeyListener(keyListener);
        }
    }

    public void removeKeyListener(KeyListener keyListener) {
        for (JComponent component : components.values()) {
            component.removeKeyListener(keyListener);
        }
    }

    private void buildEditors() {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridy = 0;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.LINE_START;

        for (PropertyWrapper property : properties) {
            if (property instanceof SpecificationProperty) {
                buildSpecificationEditor(gbc, (SpecificationProperty) property);
            } else {
                JComponent editor = FieldEditorFactory.createFieldEditor(property, null);
                buildEditor(gbc, property, editor);
            }
        }
    }
    
    
    private void buildSpecificationEditor(GridBagConstraints gbc, SpecificationProperty specificationProperty) {
        SpecificationComboBoxEditor comboBoxEditor = null;        
        Map<String, PropertyEditor> propertyEditors = new HashMap<>();
        
        for (PropertyWrapper property : specificationProperty.getProperties()) {
            JComponent editor = FieldEditorFactory.createFieldEditor(property, specificationProperty);
            if (editor instanceof SpecificationComboBoxEditor) {
                comboBoxEditor = (SpecificationComboBoxEditor) editor;
            }
            propertyEditors.put(property.getName(), new PropertyEditor(property, editor));
            buildEditor(gbc, property, editor);
        }
        
        comboBoxEditor.getSpecificationEditor().setPropertyEditors(propertyEditors);
        comboBoxEditor.getSpecificationEditor().init();
    }

    private void buildEditor(GridBagConstraints gbc, PropertyWrapper property, JComponent editor) {
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        gbc.weightx = 0;


        JLabel label = new JLabel(property.getName() + ": ");
        add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.weightx = 1;

        components.put(property.getName(), editor);
        add(editor, gbc);

        if (property.isDefaultable()) {
            gbc.gridx = 2;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;

            if (showDefaultBoxes) {
                JCheckBox checkBox = createDefaultCheckBox(property);
                add(checkBox, gbc);
            }
        }

        // Make value field focused
        if (property.getName().equalsIgnoreCase("value")) {
            componentToFocus = editor;
        }

        gbc.gridy++;
    }

    private JCheckBox createDefaultCheckBox(final PropertyWrapper property) {
        final JCheckBox checkBox = new JCheckBox();
        checkBox.setToolTipText(DEFAULT_BOX_TOOLTIP);
        checkBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox.isSelected()) {
                    defaultedProperties.add(property);
                } else {
                    defaultedProperties.remove(property);
                }
            }
        });
        return checkBox;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible && componentToFocus != null) {
            componentToFocus.requestFocusInWindow();
            componentToFocus = null;
        }
        
        super.setVisible(visible);
    }

    public Map<String, JComponent> getEditors() {
        return components;
    }


}
