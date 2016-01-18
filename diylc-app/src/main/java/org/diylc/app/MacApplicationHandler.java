package org.diylc.app;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MacApplicationHandler {

    @SuppressWarnings("deprecation")
    public static void setupMacApplication() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");

        try {
            setPreferencesHandler(new MacMenuHandler() {
                @Override
                public void handleMenuActivation() {

                }
            });

            Method setEnabledPreferencesMenu = getApplicationClass().getMethod("setEnabledPreferencesMenu", new Class[]{boolean.class});
            setEnabledPreferencesMenu.invoke(getApplication(), new Object[]{true});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Object getApplication() {
        Object application = null;

        try {
            Method getApplication = getApplicationClass().getMethod("getApplication", new Class[]{});

            application = getApplication.invoke(getApplicationClass(), new Object[]{});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return application;
    }

    public static void setPreferencesHandler(MacMenuHandler macMenuHandler) {
        setMenuHandler("com.apple.eawt.PreferencesHandler", "setPreferencesHandler", macMenuHandler);
    }

    public static void setAbouthandler(MacMenuHandler macMenuHandler) {
        setMenuHandler("com.apple.eawt.AboutHandler", "setAboutHandler", macMenuHandler);
    }

    private static void setMenuHandler(String handlerName, String setHandlerMethodName, MacMenuHandler macMenuHandler) {
        try {
            Class<?> handlerClass = Class.forName(handlerName);

            Object preferencesProxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{handlerClass}, new InvocationHandler() {

                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            macMenuHandler.handleMenuActivation();

                            return Void.class;
                        }
                    });

            Method setHandlerMethod = getApplicationClass().getMethod(setHandlerMethodName, new Class[]{handlerClass});

            setHandlerMethod.invoke(getApplication(), new Object[]{preferencesProxyInstance});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static Class<?> getApplicationClass() {
        Class<?> applicationClass = null;

        try {
            applicationClass = Class.forName("com.apple.eawt.Application");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return applicationClass;
    }
}
