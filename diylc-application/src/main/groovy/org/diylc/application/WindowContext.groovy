package org.diylc.application

import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;

class WindowContext {

    private MutableAttributeMap<Object> windowScope = new LocalAttributeMap<Object>();
    
    public MutableAttributeMap<Object> getWindowScope() {
        return windowScope
    }
}
