package org.diylc.core.properties;

import org.diylc.core.components.properties.EditableProperty;

public class AbstractMethodComponent {

    private String a;

    @EditableProperty
    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

}
