package org.diylc.app.platform;

import java.lang.reflect.Method;

import org.diylc.app.utils.ReflectionUtils;

public class MacAppEventMapper {

    private final Class<?> aboutHandlerClass;

    private final Class<?> preferencesHandlerClass;

    private final Class<?> quitHandlerClass;

    private final Class<?> quitResponseClass;

    public MacAppEventMapper() {
        aboutHandlerClass = ReflectionUtils.findRequiredClass("com.apple.eawt.AppEvent$AboutEvent");
        preferencesHandlerClass = ReflectionUtils.findRequiredClass("com.apple.eawt.AppEvent$PreferencesEvent");
        quitHandlerClass = ReflectionUtils.findRequiredClass("com.apple.eawt.AppEvent$QuitEvent");
        quitResponseClass = ReflectionUtils.findRequiredClass("com.apple.eawt.QuitResponse");
    }

    public AbstractAppResponse mapRespone(Object response) {
        AbstractAppResponse appResponse = null;
        
        if (quitResponseClass.isAssignableFrom(response.getClass())) {
            appResponse = new MacQuitResponse(response);
        }
        
        return appResponse;
    }

    public AbstractAppEvent mapEvent(Object event) {
        AbstractAppEvent appEvent = null;

        Class<?> eventClass = event.getClass();

        if (aboutHandlerClass.isAssignableFrom(eventClass)) {
            appEvent = new AboutEvent(retrieveSource(event));
        } else if (preferencesHandlerClass.isAssignableFrom(eventClass)) {
            appEvent = new PreferencesEvent(retrieveSource(event));
        } else if (quitHandlerClass.isAssignableFrom(eventClass)) {
            appEvent = new QuitEvent(retrieveSource(event));
        }

        return appEvent;
    }

    private Object retrieveSource(Object event) {
        Object eventSource = null;

        Method getSourceMethod;
        try {
            getSourceMethod = event.getClass().getMethod("getSource", new Class<?>[0]);
            eventSource = getSourceMethod.invoke(event, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return eventSource;
    }


}
