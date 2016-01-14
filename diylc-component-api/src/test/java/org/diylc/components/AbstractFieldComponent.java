package org.diylc.components;

import org.diylc.core.annotations.EditableProperty;

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
