package org.diylc.core.platform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MacQuitResponse extends AbstractAppResponse implements QuitResponse {

    public MacQuitResponse(Object response) {
        super(response);
    }

    public void performQuit() {
        try {
            Method method = getResponseClass().getMethod("performQuit", new Class<?>[0]);
            method.invoke(getResponse(), new Object[0]);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    public void cancelQuit() {
        try {
            Method method = getResponseClass().getMethod("cancelQuit", new Class<?>[0]);
            method.invoke(getResponse(), new Object[0]);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
