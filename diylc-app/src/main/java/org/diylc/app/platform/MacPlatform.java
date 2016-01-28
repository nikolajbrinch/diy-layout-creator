package org.diylc.app.platform;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.diylc.app.utils.ReflectionUtils;
import org.diylc.core.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacPlatform extends DefaultPlatform {

    private static final Logger LOG = LoggerFactory.getLogger(MacPlatform.class);
    
    private static MacPlatform macPlatform = new MacPlatform();

    private MacAppEventMapper appEventMapper = new MacAppEventMapper();

    private final Class<?> applicationClass;

    private final Object application;

    private MacPlatform() {
        this.applicationClass = ReflectionUtils.findRequiredClass("com.apple.eawt.Application");
        this.application = findApplication();
    }

    public static Platform getInstance() {
        return macPlatform;
    }

    public Object getApplication() {
        return application;
    }

    public Class<?> getApplicationClass() {
        return applicationClass;
    }

    public void setup() {
        super.setup();
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.macos.smallTabs", "true");
        System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
    }

    public void setPreferencesHandler(PreferencesHandler preferencesHandler) {
        setHandler("com.apple.eawt.PreferencesHandler", "setPreferencesHandler", new MacAppEventHandlerAdapter(preferencesHandler));

        try {
            Method setEnabledPreferencesMenu = getApplicationClass().getMethod("setEnabledPreferencesMenu", new Class[] { boolean.class });
            setEnabledPreferencesMenu.invoke(getApplication(), new Object[] { true });
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void setQuitHandler(QuitHandler quitHandler) {
        if (quitHandler == null) {
            System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
        } else {
            System.setProperty("apple.eawt.quitStrategy", "SYSTEM_EXIT_0");
        }

        setHandler("com.apple.eawt.QuitHandler", "setQuitHandler", new MacAppEventHandlerAdapter(quitHandler));
    }

    public void setAbouthandler(AboutHandler aboutHandler) {
        setHandler("com.apple.eawt.AboutHandler", "setAboutHandler", new MacAppEventHandlerAdapter(aboutHandler));
    }

    private void setHandler(String handlerName, String setHandlerMethodName, MacAppEventHandlerAdapter handler) {
        try {
            Class<?> handlerClass = Class.forName(handlerName);

            Object handlerProxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[] { handlerClass }, new InvocationHandler() {

                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            handler.handleEvent(appEventMapper.mapEvent(args[0]));

                            return Void.class;
                        }
                    });

            Method setHandlerMethod = getApplicationClass().getMethod(setHandlerMethodName, new Class[] { handlerClass });

            setHandlerMethod.invoke(getApplication(), new Object[] { handlerProxyInstance });
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Object findApplication() {
        Object application;

        try {
            Method getApplication = getApplicationClass().getMethod("getApplication", new Class[] {});

            application = getApplication.invoke(getApplicationClass(), new Object[] {});
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return application;
    }
}
