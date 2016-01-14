package org.diylc.components;

import org.diylc.core.annotations.EditableProperty;

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
