package org.diylc.core.components.registry;

import groovy.lang.GroovyClassLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codehaus.groovy.control.CompilationFailedException;
import org.diylc.core.IDIYComponent;
import org.diylc.core.ProgressView;
import org.diylc.core.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroovyComponentClassLoader extends AbstractComponentClassLoader {

    private static final Logger LOG = LoggerFactory.getLogger(GroovyComponentClassLoader.class);

    @SuppressWarnings("unchecked")
    Map<String, Class<? extends IDIYComponent>> loadGroovyClasses(ClassLoader classLoader, Path[] directories, ProgressView progressView) {
        Map<String, Class<? extends IDIYComponent>> componentClasses = new LinkedHashMap<String, Class<? extends IDIYComponent>>();

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
                progressView.update("Loading component files: " + Math.round(counter / count * 100d) + "%");
                LOG.debug("Loading component file: \"" + path.toAbsolutePath().toString() + "\"");
                try {
                    Class<?> clazz = groovyClassLoader.parseClass(path.toFile());

                    if (isValidComponentClass(clazz)) {
                        String id = (String) ReflectionUtils.getStaticProperty(clazz, "id");

                        if (id == null || id.length() <= 0) {
                            LOG.warn("No id defined for component class: " + clazz.getName() + ", component not loaded");
                        } else {
                            try {
                                @SuppressWarnings("unused")
                                UUID uuid = UUID.fromString(id);

                                Class<? extends IDIYComponent> alreadyDefinedComponentClass = componentClasses.get(id);

                                if (alreadyDefinedComponentClass != null) {
                                    LOG.warn("Id defined for component class: " + clazz.getName() + ", has already been used by component: " + alreadyDefinedComponentClass.getName() + ", component not loaded");
                                } else {
                                    componentClasses.put(id, (Class<? extends IDIYComponent>) clazz);
                                }
                            } catch (Exception e) {
                                LOG.warn("Id defined for component class: " + clazz.getName() + " is not a UUID, component not loaded");
                            }
                        }
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

        List<Path> list = new ArrayList<>(files);

        Collections.sort(list);

        return new LinkedHashSet<Path>(list);
    }
}
