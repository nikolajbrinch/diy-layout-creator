package org.diylc.components;

import org.diylc.core.annotations.EditableProperty;

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
