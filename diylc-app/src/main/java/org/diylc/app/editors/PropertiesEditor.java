package org.diylc.app.editors;

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

            JComponent editor = FieldEditorFactory.createFieldEditor(property);
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
