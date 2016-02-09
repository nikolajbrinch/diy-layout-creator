package org.diylc.components.registry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

import org.diylc.core.IDIYComponent;
import org.diylc.core.ProgressView;

public class ComponentLoader {

    private final GroovyComponentClassLoader groovyComponentClassLoader = new GroovyComponentClassLoader();

    private final ClasspathComponentClassLoader classpathComponentClassLoader = new ClasspathComponentClassLoader();

    GroovyComponentClassLoader getGroovyComponentClassLoader() {
        return groovyComponentClassLoader;
    }

    ClasspathComponentClassLoader getClasspathComponentClassLoader() {
        return classpathComponentClassLoader;
    }

    public Set<Class<? extends IDIYComponent>> loadComponents(ClassLoader classLoader, Path[] directories, ProgressView progressView) throws IOException {
        Set<Class<? extends IDIYComponent>> componentClasses = new TreeSet<Class<? extends IDIYComponent>>(new ComponentComparator());

        /*
         * Load from directories if there are any
         */
        if (directories.length > 0) {
            componentClasses.addAll(getGroovyComponentClassLoader().loadGroovyClasses(classLoader, directories, progressView));
        }

        /*
         * If nothing got loaded, load from classpath
         */
        if (componentClasses.isEmpty()) {
            componentClasses.addAll(getClasspathComponentClassLoader().loadCompiledClasses(classLoader, progressView));
        }

        return componentClasses;
    }

}
