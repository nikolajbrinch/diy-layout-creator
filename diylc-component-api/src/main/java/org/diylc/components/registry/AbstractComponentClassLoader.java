package org.diylc.components.registry;

import java.lang.reflect.Modifier;

import org.diylc.core.EventType;
import org.diylc.core.IDIYComponent;
import org.diylc.core.events.EventDispatcher;

public abstract class AbstractComponentClassLoader {

    public static final String COMPONENT_PACKAGE_NAME = "org.diylc.components";

    public static final String JAR_EXTENSION = ".jar";

    public static final String GROOVY_EXTENSION = ".groovy";

    public static final String CLASS_EXTENSION = ".class";

    private final EventDispatcher<EventType> eventDispatcher = new EventDispatcher<>();

    protected EventDispatcher<EventType> getEventDispatcher() {
        return eventDispatcher;
    }

    protected boolean isValidComponentClass(Class<?> clazz) {
        return (!Modifier.isAbstract(clazz.getModifiers())) && (IDIYComponent.class.isAssignableFrom(clazz));
    }

}
