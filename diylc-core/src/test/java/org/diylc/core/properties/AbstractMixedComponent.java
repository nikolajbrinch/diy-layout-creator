package org.diylc.core.properties;

import org.diylc.core.components.properties.EditableProperty;

public class AbstractMixedComponent {

    @EditableProperty(name = "C", setter = "setC", getter = "getC")
    private String a;

    public String getC() {
        return a;
    }

    public void setC(String a) {
        this.a = a;
    }

}
