package org.diylc.core.platform;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.diylc.core.PropertyWrapper;
import org.diylc.core.utils.Constants;

public class DefaultFontEditor extends FontChooserComboBox {

    private static final long serialVersionUID = 1L;

    private Color oldBg = getBackground();

    private final PropertyWrapper property;

    private Font oldFont;

    public DefaultFontEditor(final PropertyWrapper property) {
        this.property = property;
        this.oldFont = (Font) property.getValue();
        setSelectedItem(((Font) property.getValue()).getName());
        addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    DefaultFontEditor.this.property.setChanged(true);
                    setBackground(oldBg);
                    Font oldFont = (Font) DefaultFontEditor.this.property.getValue();
                    Font newFont = new Font(getSelectedItem().toString(), oldFont.getStyle(),
                            oldFont.getSize());
                    DefaultFontEditor.this.property.setValue(newFont);
                    updateValue();
                }
            }
        });
        if (!property.isUnique()) {
            setBackground(Constants.MULTI_VALUE_COLOR);
        }
    }

    protected void updateValue() {
        Font newFont = (Font) DefaultFontEditor.this.property.getValue();
        
        if (!newFont.equals(oldFont)) {
            firePropertyChange(DefaultFontEditor.this.property.getName(), oldFont, newFont);
        }
        
        oldFont = newFont;
    }
}
