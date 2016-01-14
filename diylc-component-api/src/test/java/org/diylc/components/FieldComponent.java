package org.diylc.components;

import org.diylc.core.annotations.EditableProperty;

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
