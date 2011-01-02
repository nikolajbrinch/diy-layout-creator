package com.diyfever.diylc.gui.editor;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.diyfever.diylc.common.PropertyWrapper;
import com.diyfever.gui.ButtonDialog;

public class PropertyEditorDialog extends ButtonDialog {

	private static final Logger LOG = Logger.getLogger(PropertyEditorDialog.class);

	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_BOX_TOOLTIP = "<html>If this box is checked application will use the current value as a<br>default when creating new components of the same type</html>";

	private List<PropertyWrapper> properties;
	private Set<PropertyWrapper> defaultedProperties;

	public PropertyEditorDialog(JFrame owner, List<PropertyWrapper> properties) {
		super(owner, "Editor", new String[] { ButtonDialog.OK, ButtonDialog.CANCEL });

		LOG.debug("Creating property editor for: " + properties);

		this.properties = properties;
		this.defaultedProperties = new HashSet<PropertyWrapper>();

		setMinimumSize(new Dimension(240, 40));

		layoutGui();
		setLocationRelativeTo(owner);
		//
		// setModal(true);
		//
		// JPanel holderPanel = new JPanel();
		// holderPanel.setLayout(new BorderLayout());
		// holderPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		//
		// holderPanel.add(createEditorFields(), BorderLayout.CENTER);
		// holderPanel.add(createButtonPanel(), BorderLayout.SOUTH);
		//
		// setContentPane(holderPanel);
		// 
		//
		// pack();
		// setLocationRelativeTo(owner);
	}

	private JPanel createEditorFields() {
		JPanel editorPanel = new JPanel(new GridBagLayout());
		editorPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridy = 0;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.LINE_START;

		for (PropertyWrapper property : properties) {
			gbc.gridx = 0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0;

			editorPanel.add(new JLabel(property.getName() + ": "), gbc);

			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;

			editorPanel.add(FieldEditorFactory.createFieldEditor(property), gbc);

			if (property.isDefaultable()) {
				gbc.gridx = 2;
				gbc.fill = GridBagConstraints.NONE;
				gbc.weightx = 0;

				editorPanel.add(createDefaultCheckBox(property), gbc);
			}

			gbc.gridy++;
		}

		return editorPanel;
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

	public Set<PropertyWrapper> getDefaultedProperties() {
		return defaultedProperties;
	}

	public static boolean showFor(JFrame owner, List<PropertyWrapper> properties) {
		PropertyEditorDialog editor = new PropertyEditorDialog(owner, properties);
		editor.setVisible(true);
		if (OK.equals(editor.getSelectedButtonCaption())) {
			return true;
		}
		return false;
	}

	@Override
	protected JComponent getMainComponent() {
		return createEditorFields();
	}
}
