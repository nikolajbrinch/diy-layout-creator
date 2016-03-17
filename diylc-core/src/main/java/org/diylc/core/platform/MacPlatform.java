package org.diylc.core.platform;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Path;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.filechooser.FileFilter;

import org.diylc.core.components.properties.PropertyDescriptor;
import org.diylc.core.utils.ReflectionUtils;
import org.diylc.core.utils.SystemUtils;

public class MacPlatform extends DefaultPlatform {

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
        System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
        System.setProperty("com.apple.mrj.application.live-resize", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "DIY Layout Creator");
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
                            if (args.length > 1) {
                                handler.handleEvent(appEventMapper.mapEvent(args[0]), appEventMapper.mapRespone(args[1]));
                            } else {
                                handler.handleEvent(appEventMapper.mapEvent(args[0]), null);
                            }

                            return Void.class;
                        }
                    });

            callSet(getApplication(), setHandlerMethodName, handlerClass, handlerProxyInstance);
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

    @Override
    public String getDefaultDisplayFontName() {
        return SystemUtils.getOsMinorVersion() >= 10 ? "Helvetica Neue" : "Lucida Grande";
    }

    @Override
    public String getDefaultMonospacedFontName() {
        return "Menlo";
    }

    @Override
    public String getDefaultTextFontName() {
        return "Arial";
    }

    @Override
    public SaveDialog createSaveDialog(JFrame parent, Path lastDirectory, Path initialFile, FileFilter filter, String defaultExtension) {
        return new MacSaveDialog(parent, lastDirectory, initialFile, new FilenameFilterAdapter(filter), defaultExtension);
    }

    @Override
    public OpenDialog createOpenDialog(JFrame mainFrame, Path lastDirectory, Path initialFile, FileFilter filter, String defaultExtension,
            IFileChooserAccessory accessory) {
        return new MacOpenDialog(mainFrame, lastDirectory, initialFile, new FilenameFilterAdapter(filter), defaultExtension);
    }

    @Override
    public JComponent createFontEditor(PropertyDescriptor property) {
        return new MacFontEditor(property);
    }

    @Override
    public void setDefaultMenuBar(JMenuBar jMenuBar) {
        callSet(getApplication(), "setDefaultMenuBar", JMenuBar.class, jMenuBar);
    }

    private void callSet(Object object, String methodName, Class<?> clazz, Object value) {
        try {
            Method method = object.getClass().getMethod(methodName, new Class[] { clazz });
            method.invoke(object, new Object[] { value });
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
