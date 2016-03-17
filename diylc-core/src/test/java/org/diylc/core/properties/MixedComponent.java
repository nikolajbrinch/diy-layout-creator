package org.diylc.core.properties;

import org.diylc.core.components.properties.EditableProperty;

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
