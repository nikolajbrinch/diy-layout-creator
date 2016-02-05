package org.diylc.app.view.editors;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.diylc.core.PropertyWrapper;
import org.diylc.core.utils.Constants;

public class StringEditor extends JTextField {

    private static final long serialVersionUID = 1L;

    private Color oldBg = getBackground();

    private final PropertyWrapper property;

    private String oldValue;

    public StringEditor(PropertyWrapper property) {
        super(property.getValue() == null ? "" : (String) property.getValue());
        this.property = property;
        this.oldValue = getText();
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
        getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                textChanged();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                textChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textChanged();
            }
        });
        if (!property.isUnique()) {
            setBackground(Constants.MULTI_VALUE_COLOR);
        }
    }

    protected void updateValue() {
        String newValue = (String) property.getValue();
        firePropertyChange(property.getName(), oldValue, newValue);
        oldValue = newValue;
    }

    private void textChanged() {
        property.setChanged(true);
        setBackground(oldBg);
        property.setValue(getText());
    }
}
