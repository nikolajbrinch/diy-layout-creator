package org.diylc.app.view.properties;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.diylc.app.view.editors.PropertiesEditor;
import org.diylc.core.PropertyWrapper;

/**
 * @author nikolajbrinch@gmail.com
 */
public class PropertyView extends JComponent {

    private static final long serialVersionUID = 1L;

    private final PropertyController controller;

    private Color labelBackgroundColor = null;

    private Color borderColor = null;

    private JPanel propertyContainer = null;

    private PropertiesEditor propertyEditor = null;

    public PropertyView(PropertyController controller) {
        super();
        this.controller = controller;
        setFocusable(true);
        setPreferredSize(new Dimension(400, 800));
        setBorder(createDefaultBorder());
        setLayout(new BorderLayout());

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BorderLayout());
        labelPanel.setBackground(getLabelBackgroundColor());
        labelPanel.setBorder(createDefaultBorder());

        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BorderLayout());
        toolbar.setBackground(getLabelBackgroundColor());
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));
        labelPanel.add(toolbar, BorderLayout.WEST);

        JLabel label = new JLabel("Properties");
        label.setFont(getLabelFont().deriveFont(getLabelFont().getSize2D() - 2f));

        label.setBorder(BorderFactory.createEmptyBorder());
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setHorizontalTextPosition(SwingConstants.LEFT);

        JButton button = new JButton(Character.toString((char) 9654));
        button.setFont(getLabelFont().deriveFont(getLabelFont().getSize2D() - 3f));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addActionListener((ActionEvent e) -> getController().closePanel());
        toolbar.add(button);
        toolbar.add(label);

        add(labelPanel, BorderLayout.NORTH);

        propertyContainer = new JPanel();
        propertyContainer.setLayout(new BorderLayout());
        add(propertyContainer, BorderLayout.CENTER);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    private Paint getBorderColor() {
        if (borderColor == null) {
            borderColor = getLabelBackgroundColor().darker();

        }
        return borderColor;
    }

    private Color getLabelBackgroundColor() {
        if (labelBackgroundColor == null) {
            Color backgroundColor = getPanelBackgroundColor();
            labelBackgroundColor = new Color(backgroundColor.getRed() - 8, backgroundColor.getGreen() - 8, backgroundColor.getBlue() - 8,
                    backgroundColor.getAlpha());
        }

        return labelBackgroundColor;
    }

    private Color getPanelBackgroundColor() {
        return UIManager.getColor("Panel.background");
    }

    private Border createDefaultBorder() {
        return BorderFactory.createStrokeBorder(new BasicStroke(0.2f), getBorderColor());
    }

    public PropertyController getController() {
        return controller;
    }

    private Font getLabelFont() {
        return UIManager.getFont("Label.font");
    }

    public void displayProperties(List<PropertyWrapper> properties) {
        propertyContainer.removeAll();

        if (properties != null && !properties.isEmpty()) {
            propertyEditor = new PropertiesEditor(properties);
            Map<String, JComponent> editors = propertyEditor.getEditors();
            for (JComponent editor : editors.values()) {
                editor.addPropertyChangeListener((PropertyChangeEvent event) -> {
                    PropertyWrapper property = findProperty(properties, event.getPropertyName());
                    if (property != null) {
                        Object newValue = translateValue(event.getNewValue(), property, properties);

                        getController().updateProperty(property, event.getOldValue(), newValue);
                    }
                });
            }
            propertyContainer.add(propertyEditor, BorderLayout.NORTH);
        } else {
            propertyContainer.add(new JPanel(), BorderLayout.NORTH);
        }

        revalidate();
        repaint();
    }

    private Object translateValue(Object value, PropertyWrapper property, List<PropertyWrapper> properties) {
        if (property.getName().equals("Font")) {
            Font newFont = (Font) value;
            PropertyWrapper sizeProperty = findProperty(properties, "Font Size");
            PropertyWrapper boldProperty = findProperty(properties, "Font Bold");
            PropertyWrapper italicProperty = findProperty(properties, "Font Italic");

            float size = ((Integer) sizeProperty.getValue()).floatValue();
            boolean bold = (boolean) boldProperty.getValue();
            boolean italic = (boolean) italicProperty.getValue();
            int style = bold ? Font.BOLD : Font.PLAIN;
            style += italic ? Font.ITALIC : Font.PLAIN;

            value = newFont.deriveFont(style, size);
        }

        return value;

    }

    private PropertyWrapper findProperty(List<PropertyWrapper> properties, String propertyName) {
        PropertyWrapper property = null;

        for (PropertyWrapper propertyWrapper : properties) {
            if (propertyWrapper.getName().equals(propertyName)) {
                property = propertyWrapper;
            }
        }

        return property;
    }
}
