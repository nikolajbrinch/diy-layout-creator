package org.diylc.core.platform;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.diylc.core.PropertyWrapper;
import org.diylc.core.utils.Constants;

public class MacFontEditor extends MacFontChooserComboBox {

	private static final long serialVersionUID = 1L;

	private Color oldBg = getBackground();

	private final PropertyWrapper property;

    private Font oldFont;

	public MacFontEditor(final PropertyWrapper property) {
		this.property = property;
		this.oldFont = (Font) property.getValue();
		setSelectedItem(((Font) property.getValue()).getName());
		addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					MacFontEditor.this.property.setChanged(true);
					setBackground(oldBg);
					Font oldFont = (Font) MacFontEditor.this.property.getValue();
					Font newFont = new Font(getSelectedItem().toString(), oldFont.getStyle(),
							oldFont.getSize());
					MacFontEditor.this.property.setValue(newFont);
				}
			}
		});
		addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                updateValue();
            }
            
        });
		addFocusListener(new FocusAdapter() {
            
            @Override
            public void focusLost(FocusEvent e) {
                updateValue();
                
            }
            
        });
		if (!property.isUnique()) {
			setBackground(Constants.MULTI_VALUE_COLOR);
		}
	}

    protected void updateValue() {
        Font newFont = (Font) MacFontEditor.this.property.getValue();
        
        if (!newFont.equals(oldFont)) {
            firePropertyChange(MacFontEditor.this.property.getName(), oldFont, newFont);
        }
        
        oldFont = newFont;
    }
}
