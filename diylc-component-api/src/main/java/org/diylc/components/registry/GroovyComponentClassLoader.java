package org.diylc.components.registry;

import groovy.lang.GroovyClassLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codehaus.groovy.control.CompilationFailedException;
import org.diylc.core.EventType;
import org.diylc.core.IDIYComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroovyComponentClassLoader extends AbstractComponentClassLoader {
    
    private static final Logger LOG = LoggerFactory.getLogger(GroovyComponentClassLoader.class);

    @SuppressWarnings("unchecked")
    Collection<? extends Class<? extends IDIYComponent>> loadGroovyClasses(ClassLoader classLoader, Path[] directories) {
        Set<Class<? extends IDIYComponent>> componentClasses = new HashSet<Class<? extends IDIYComponent>>();

        GroovyClassLoader groovyClassLoader = null;

        try {
            groovyClassLoader = new GroovyClassLoader(classLoader);

            Set<Path> componentTypeFiles = null;

            try {
                componentTypeFiles = getGroovyFiles(directories);
            } catch (Exception e) {
                e.printStackTrace();
            }

            double count = componentTypeFiles.size();
            double counter = 1;
            for (Path path : componentTypeFiles) {
                getEventDispatcher().sendEvent(EventType.SPLASH_UPDATE,
                        "Loading component files: " + Math.round(counter / count * 100d) + "%");
                LOG.debug("Loading component file: \"" + path.toAbsolutePath().toString() + "\"");
                try {
                    Class<?> clazz = groovyClassLoader.parseClass(path.toFile());

                    if (isValidComponentClass(clazz)) {
                        componentClasses.add((Class<? extends IDIYComponent>) clazz);
                    }
                } catch (CompilationFailedException e) {
                    LOG.warn("Compilation failed", e);
                } catch (IOException e) {
                    LOG.warn("IOException loading class", e);
                }
                counter++;
            }
        } finally {
            try {
                groovyClassLoader.close();
            } catch (IOException e) {
                LOG.warn("IOException closing GroovyClassLoader", e);
            }
        }

        return componentClasses;
    }
    
    private Set<Path> getGroovyFiles(Path[] directories) throws IOException {
        Set<Path> files = new HashSet<Path>();

        for (Path directory : directories) {
            if (Files.exists(directory) && Files.isDirectory(directory)) {

                try (Stream<Path> stream = Files.find(directory,Integer.MAX_VALUE,
                        (path, attr) -> {
                            return Files.isRegularFile(path) && path.toString().endsWith(GROOVY_EXTENSION);   
                        })) {
                    files.addAll(stream.collect(Collectors.toList()));
                }
            }
        }

        return files;
    }
}
