package org.diylc.components.registry;

import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

import org.diylc.core.IDIYComponent;

public class ComponentLoader {

    private final GroovyComponentClassLoader groovyComponentClassLoader = new GroovyComponentClassLoader();

    private final ClasspathComponentClassLoader classpathComponentClassLoader = new ClasspathComponentClassLoader();

    GroovyComponentClassLoader getGroovyComponentClassLoader() {
        return groovyComponentClassLoader;
    }

    ClasspathComponentClassLoader getClasspathComponentClassLoader() {
        return classpathComponentClassLoader;
    }

    public Set<Class<? extends IDIYComponent>> loadComponents(ClassLoader classLoader, Path[] directories) {
        Set<Class<? extends IDIYComponent>> componentClasses = new TreeSet<Class<? extends IDIYComponent>>(new ComponentComparator());

        /*
         * Load from directories if there are any
         */
        if (directories.length > 0) {
            componentClasses.addAll(getGroovyComponentClassLoader().loadGroovyClasses(classLoader, directories));
        }

        /*
         * If nothing got loaded, load from classpath
         */
        if (componentClasses.isEmpty()) {
            componentClasses.addAll(getClasspathComponentClassLoader().loadCompiledClasses(classLoader));
        }

        return componentClasses;
    }

}
