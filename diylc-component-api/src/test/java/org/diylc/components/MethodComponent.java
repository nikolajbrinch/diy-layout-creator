package org.diylc.components;

import org.diylc.core.annotations.EditableProperty;

public class MethodComponent extends AbstractMethodComponent {

    private int b;

    @EditableProperty
    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

}
