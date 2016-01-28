package org.diylc.app.dialogs;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.diylc.app.editors.PropertiesEditor;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyEditorDialog extends ButtonDialog {

	private static final Logger LOG = LoggerFactory.getLogger(PropertyEditorDialog.class);

	private static final long serialVersionUID = 1L;

	private List<PropertyWrapper> properties;

	private Set<PropertyWrapper> defaultedProperties;

	public PropertyEditorDialog(JFrame owner, List<PropertyWrapper> properties,
			String title) {
		super(owner, title,
				new String[] { ButtonDialog.OK, ButtonDialog.CANCEL });

		LOG.debug("Creating property editor for: " + properties);

		this.properties = properties;
		this.defaultedProperties = new HashSet<PropertyWrapper>();

		setMinimumSize(new Dimension(240, 40));

		layoutGui();
		setLocationRelativeTo(owner);
	}

	@Override
	protected boolean validateInput(String button) {
		if (button.equals(ButtonDialog.OK)) {
			for (PropertyWrapper property : properties) {
				try {
					property.getValidator().validate(property.getValue());
				} catch (ValidationException ve) {
					JOptionPane.showMessageDialog(PropertyEditorDialog.this,
							"Input error for \"" + property.getName() + "\": " + ve.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
					
					return false;
				}
			}
		}
		
		return true;
	}

	public Set<PropertyWrapper> getDefaultedProperties() {
		return defaultedProperties;
	}

	public static boolean showFor(JFrame owner, List<PropertyWrapper> properties, String title) {
		PropertyEditorDialog editor = new PropertyEditorDialog(owner, properties, title);
		editor.setVisible(true);
		
		if (OK.equals(editor.getSelectedButtonCaption())) {
			return true;
		}
		
		return false;
	}

	@Override
	protected JComponent getMainComponent() {
	    PropertiesEditor editorPanel = new PropertiesEditor(properties, defaultedProperties);
        editorPanel.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    getButton(OK).doClick();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    getButton(CANCEL).doClick();
                }
            }
        });

		return editorPanel; 
	}
}
