package org.diylc.bootstrap;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Bootstrap {

    static File path = new File("/Users/neko/dev/projects/github/diy-layout-creator/diylc-app/build/macApp/DIYLC4.app/Contents/Java/");

    static File appPath = new File("/Users/neko/dev/projects/github/diy-layout-creator/diylc-app/build/libs/");

    private static URL constructUrl(File path, String name) {
        try {
            return new File(path, name).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static URL[] toUrls(String[] names) {
        return Arrays.stream(names).map((name) -> constructUrl(path, name)).collect(Collectors.toList()).toArray(new URL[0]);
    }

    private static URL[] toUrls(File path, String[] names) {
        return Arrays.stream(names).map((name) -> constructUrl(path, name)).collect(Collectors.toList()).toArray(new URL[0]);
    }

    public static void main(String[] args) throws Exception {
        ClassLoader dependencyLoader = loadDependencies();
        ClassLoader libraryLoader = loadLibrary(dependencyLoader);
        ClassLoader appLoader = loadApplication(libraryLoader);

        Class<?> classToLoad = Class.forName("org.diylc.app.DIYLCStarter", true, appLoader);
        Method method = findMethod(classToLoad, "main");
        method.invoke(null, new Object[] { args });
    }

    private static ClassLoader loadApplication(ClassLoader libraryLoader) {
        String[] application = new String[] { "diylc-app-4.0.0-SNAPSHOT.jar.original" };

        return new URLClassLoader(toUrls(appPath, application), libraryLoader);
    }

    private static ClassLoader loadLibrary(ClassLoader coreLoader) {
        String[] components = new String[] { "diylc-component-library-4.0.0-SNAPSHOT.jar" };

        return new URLClassLoader(toUrls(components), coreLoader);
    }

    private static ClassLoader loadDependencies() {
        String[] dependencies = new String[] { "commons-collections4-4.1.jar", "groovy-all-2.4.7.jar", "itext-2.1.7.jar",
                "jackson-annotations-2.8.4.jar", "jackson-core-2.8.4.jar", "jackson-databind-2.8.4.jar", "jcl-over-slf4j-1.7.21.jar",
                "jettison-1.3.8.jar", "jul-to-slf4j-1.7.21.jar", "log4j-core-2.7.jar", "log4j-slf4j-impl-2.7.jar",
                "org-netbeans-api-annotations-common-RELEASE82.jar", "org-openide-awt-RELEASE82.jar",
                "org-openide-filesystems-RELEASE82.jar", "org-openide-util-lookup-RELEASE82.jar", "org-openide-util-RELEASE82.jar",
                "org-openide-util-ui-RELEASE82.jar", "poi-3.15.jar", "slf4j-api-1.7.21.jar", "snakeyaml-1.17.jar",
                "spring-aop-4.3.4.RELEASE.jar", "spring-beans-4.3.4.RELEASE.jar", "spring-boot-1.4.2.RELEASE.jar",
                "spring-boot-autoconfigure-1.4.2.RELEASE.jar", "spring-boot-starter-1.4.2.RELEASE.jar",
                "spring-boot-starter-log4j2-1.4.2.RELEASE.jar", "spring-context-4.3.4.RELEASE.jar", "spring-core-4.3.4.RELEASE.jar",
                "spring-expression-4.3.4.RELEASE.jar", "xstream-1.4.9.jar", "diylc-core-4.0.0-SNAPSHOT.jar",
                "diylc-component-api-4.0.0-SNAPSHOT.jar" };

        return new URLClassLoader(toUrls(dependencies));
    }

    public static Method findMethod(Class<?> clazz, String name) {
        return Arrays.stream(clazz.getMethods()).filter((m) -> m.getName().equals(name)).findFirst().get();
    }

}
