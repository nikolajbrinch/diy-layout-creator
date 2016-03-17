package org.diylc.core.properties;

import org.diylc.core.components.properties.EditableProperty;

public class AbstractFieldComponent {

    @EditableProperty
    private String a;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

}
