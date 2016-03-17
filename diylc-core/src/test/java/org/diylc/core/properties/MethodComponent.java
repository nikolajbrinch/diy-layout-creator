package org.diylc.core.properties;

import org.diylc.core.components.properties.EditableProperty;

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
