package org.diylc.components;

import org.diylc.core.annotations.EditableProperty;

public class MixedComponent extends AbstractMixedComponent {

    private int b;

    @EditableProperty(name = "D", setter = "setD", getter = "getD")
    public int getD() {
        return b;
    }

    public void setD(int b) {
        this.b = b;
    }

}
