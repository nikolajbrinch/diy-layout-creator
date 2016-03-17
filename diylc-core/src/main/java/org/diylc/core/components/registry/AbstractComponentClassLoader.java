package org.diylc.core.components.registry;

import org.diylc.core.components.IDIYComponent;

import java.lang.reflect.Modifier;

public abstract class AbstractComponentClassLoader {

    public static final String COMPONENT_PACKAGE_NAME = "org.diylc.components";

    public static final String GROOVY_EXTENSION = ".groovy";

    public static final String CLASS_EXTENSION = ".class";

    protected boolean isValidComponentClass(Class<?> clazz) {
        return (!Modifier.isAbstract(clazz.getModifiers())) && (IDIYComponent.class.isAssignableFrom(clazz));
    }

}
