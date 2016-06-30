package org.diylc.application

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope
import org.springframework.webflow.core.collection.MutableAttributeMap;

import groovy.transform.CompileStatic;

@CompileStatic
class WindowScope implements Scope {

        /**
     * Logger, usable by subclasses.
     */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        MutableAttributeMap<Object> scope = getScope();
        Object scopedObject = scope.get(name);
        if (scopedObject == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("No scoped instance '" + name + "' found; creating new instance");
            }
            scopedObject = objectFactory.getObject();
            scope.put(name, scopedObject);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Returning scoped instance '" + name + "'");
            }
        }
        return scopedObject;
    }

    @Override
    public Object remove(String name) {
        return getScope().remove(name);
    }

    private MutableAttributeMap<Object> getScope() {
        return WindowManager.getWindowContext().getWindowScope()
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }

}
