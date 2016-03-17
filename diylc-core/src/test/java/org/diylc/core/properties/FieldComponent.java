package org.diylc.core.properties;

import org.diylc.core.components.properties.EditableProperty;

public class FieldComponent extends AbstractFieldComponent {

    @EditableProperty
    private int b;

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

}
