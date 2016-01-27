package org.diylc.app.view.properties;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Paint;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.diylc.app.dialogs.properties.PropertiesEditorPanel;
import org.diylc.core.PropertyWrapper;

public class PropertyPanel extends JComponent {

    private static final long serialVersionUID = 1L;

    private final PropertyController controller;

    private Color labelBackgroundColor = null;

    private Color borderColor = null;

    private JPanel propertyContainer = null;

    private PropertiesEditorPanel propertyEditor = null;

    public PropertyPanel(PropertyController controller) {
        super();
        this.controller = controller;
        setFocusable(true);
        setPreferredSize(new Dimension(300, 800));
        setBorder(createDefaultBorder());
        setLayout(new BorderLayout());
        LabelPanel labelPanel = new LabelPanel();
        Label label = new Label("PROPERTIES");
        labelPanel.add(label, BorderLayout.WEST);
        add(labelPanel, BorderLayout.NORTH);

        propertyContainer = new JPanel();
        propertyContainer.setLayout(new BorderLayout());
        add(propertyContainer, BorderLayout.CENTER);

        // JButton button = new JButton("Apply");
        // JPanel buttonPanel = new JPanel();
        // buttonPanel.setLayout(new BorderLayout());
        // buttonPanel.add(button, BorderLayout.EAST);
        // add(buttonPanel, BorderLayout.SOUTH);
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

    class Label extends JLabel {

        private static final long serialVersionUID = 1L;

        private Font labelFont = null;

        public Label(String text) {
            setText(text);
            setFont(getLabelFont());
            setBorder(BorderFactory.createEmptyBorder());
            setHorizontalAlignment(SwingConstants.LEFT);
            setHorizontalTextPosition(SwingConstants.LEFT);
        }

        private Font getLabelFont() {
            if (labelFont == null) {
                Font font = UIManager.getFont("Label.font");
                labelFont = font.deriveFont(font.getSize2D() - 3);
            }

            return labelFont;
        }
    }

    class LabelPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        public LabelPanel() {
            setLayout(new BorderLayout());
            setBackground(getLabelBackgroundColor());
            setBorder(createDefaultBorder());
        }
    }

    public void displayProperties(List<PropertyWrapper> properties) {
        propertyContainer.removeAll();

        if (properties != null && !properties.isEmpty()) {
            propertyEditor = new PropertiesEditorPanel(properties);
            Map<String, JComponent> editors = propertyEditor.getEditors();
            for (JComponent editor : editors.values()) {
                editor.addPropertyChangeListener((PropertyChangeEvent event) -> {
                    PropertyWrapper property = findProperty(properties, event.getPropertyName());
                    if (property != null) {
                        getController().updateProperty(property, event.getOldValue(), event.getNewValue());
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
