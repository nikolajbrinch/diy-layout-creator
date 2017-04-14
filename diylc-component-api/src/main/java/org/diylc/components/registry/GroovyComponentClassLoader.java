package org.diylc.components.registry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codehaus.groovy.control.CompilationFailedException;
import org.diylc.core.IDIYComponent;
import org.diylc.core.ProgressView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.GroovyClassLoader;

public class GroovyComponentClassLoader extends AbstractComponentClassLoader {
    
    private static final Logger LOG = LoggerFactory.getLogger(GroovyComponentClassLoader.class);

    private AtomicInteger counter = new AtomicInteger();
    
    @SuppressWarnings("unchecked")
    Class<? extends IDIYComponent> loadGroovyClass(GroovyClassLoader groovyClassLoader, ProgressView progressView, double count, Path componentTypeFile) {
        Class<? extends IDIYComponent> componentClass = null;
        
        progressView.update("Loading component files: " + Math.round(counter.doubleValue() / count * 100d) + "%");
        LOG.trace("Loading component file: \"" + componentTypeFile.toAbsolutePath().toString() + "\"");
        try {
            Class<?> clazz = groovyClassLoader.parseClass(componentTypeFile.toFile());

            if (isValidComponentClass(clazz)) {
                componentClass = (Class<? extends IDIYComponent>) clazz;
            }
        } catch (CompilationFailedException e) {
            LOG.warn("Compilation failed", e);
        } catch (IOException e) {
            LOG.warn("IOException loading class", e);
        }
        counter.getAndIncrement();
        
        return componentClass;
    }
    
    Collection<? extends Class<? extends IDIYComponent>> loadGroovyClasses(ClassLoader classLoader, Path[] directories, ProgressView progressView) {
        Set<Class<? extends IDIYComponent>> componentClasses = new HashSet<Class<? extends IDIYComponent>>();

        try (GroovyClassLoader groovyClassLoader = new GroovyClassLoader(classLoader)) {

            Set<Path> componentTypeFiles = null;

            try {
                componentTypeFiles = getGroovyFiles(directories);
            } catch (Exception e) {
                e.printStackTrace();
            }

            double count = componentTypeFiles.size();
            counter.set(1);
            componentClasses = componentTypeFiles.parallelStream()
                .map((tf) -> loadGroovyClass(groovyClassLoader, progressView, count, tf))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        } catch (IOException e) {
            LOG.warn("IOException closing GroovyClassLoader", e);
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
                    files.addAll(stream.parallel().collect(Collectors.toList()));
                }
            }
        }

        return files;
    }
}
