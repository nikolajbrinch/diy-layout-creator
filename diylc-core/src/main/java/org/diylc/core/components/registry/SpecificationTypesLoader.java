package org.diylc.core.components.registry;

import groovy.lang.GroovyClassLoader;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codehaus.groovy.control.CompilationFailedException;
import org.diylc.core.components.properties.PropertyModel;
import org.diylc.core.components.properties.PropertyModelDescriptor;
import org.diylc.core.resources.Resource;
import org.diylc.core.resources.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpecificationTypesLoader {

    private static final Logger LOG = LoggerFactory.getLogger(SpecificationTypesLoader.class);

    public static final String SPECIFICATION_PACKAGE_NAME = "org.diylc.specifications";

    public static final String GROOVY_EXTENSION = ".groovy";

    public static final String CLASS_EXTENSION = ".class";

    private ResourceLoader resourceLoader = new ResourceLoader();

    public SpecificationTypeRegistry loadSpecificationTypes(ClassLoader classLoader, Path[] directories) throws IOException {
        LOG.info("Loading specification types.");
        SpecificationTypeRegistry specificationTypeRegistry = new SpecificationTypeRegistry();

        Set<Class<? extends PropertyModel>> specificationTypes = findSpecificationTypes(classLoader, directories);

        for (Class<? extends PropertyModel> specificationType : specificationTypes) {
            if (specificationType.isAnnotationPresent(PropertyModelDescriptor.class)) {
                PropertyModelDescriptor annotation = specificationType.getAnnotation(PropertyModelDescriptor.class);
                String category = annotation.category();
                specificationTypeRegistry.add(category, specificationType);
            }
        }

        return specificationTypeRegistry;
    }

    private Set<Class<? extends PropertyModel>> findSpecificationTypes(ClassLoader classLoader, Path[] directories) throws IOException {
        Set<Class<? extends PropertyModel>> specificationClasses = new HashSet<Class<? extends PropertyModel>>();

        if (directories.length > 0) {
            specificationClasses.addAll(findGroovySpecificationTypes(classLoader, directories));
        }
        
        if (specificationClasses.isEmpty()) {
            specificationClasses.addAll(findClasspathSpecificationTypes(classLoader));
        }

        return specificationClasses;
    }

    @SuppressWarnings("unchecked")
    private Set<Class<? extends PropertyModel>> findGroovySpecificationTypes(ClassLoader classLoader, Path[] directories) {
        Set<Class<? extends PropertyModel>> specificationClasses = new HashSet<Class<? extends PropertyModel>>();

        GroovyClassLoader groovyClassLoader = null;

        try {
            groovyClassLoader = new GroovyClassLoader(classLoader);

            for (Path directory : directories) {
                groovyClassLoader.addClasspath(directory.toString());
            }
            
            Set<Path> specificationTypeFiles = null;

            try {
                specificationTypeFiles = getGroovyFiles(directories);
            } catch (Exception e) {
                LOG.error("Error getting groovy files", e);
            }

            for (Path path : specificationTypeFiles) {
                LOG.debug("Loading specification file: \"" + path.toAbsolutePath().toString() + "\"");
                try {
                    Class<?> clazz = groovyClassLoader.parseClass(path.toFile());

                    try {
                        Class<?> alreadyLoadedClass = ClassLoader.getSystemClassLoader().loadClass(clazz.getName());
                        clazz = alreadyLoadedClass;
                        LOG.debug("Class " + clazz.getName() + " is already present in application classloader.");
                    } catch (ClassNotFoundException e) {
                        /*
                         * Ignore
                         */
                    }
                    
                    if (isValidSpecificationClass(clazz)) {
                        specificationClasses.add((Class<? extends PropertyModel>) clazz);
                    }
                } catch (CompilationFailedException e) {
                    LOG.warn("Compilation failed", e);
                } catch (IOException e) {
                    LOG.warn("IOException loading class", e);
                }
            }
        } finally {
            try {
                groovyClassLoader.close();
            } catch (IOException e) {
                LOG.warn("IOException closing GroovyClassLoader", e);
            }
        }

        return specificationClasses;
    }

    @SuppressWarnings("unchecked")
    private Set<Class<? extends PropertyModel>> findClasspathSpecificationTypes(ClassLoader classLoader) throws IOException {
        Set<Class<? extends PropertyModel>> specificationClasses = new HashSet<Class<? extends PropertyModel>>();

        Set<Resource> classResources = resourceLoader.getResources(classLoader, SPECIFICATION_PACKAGE_NAME, CLASS_EXTENSION);

        for (Resource resource : classResources) {
            LOG.debug("Loading specification class: \"" + resource + "\"");
            try {
                String filename = resource.getFilename();
                Class<?> clazz = Class.forName(filename.substring(0, filename.lastIndexOf('.')).replace('/', '.'));

                if (isValidSpecificationClass(clazz)) {
                    specificationClasses.add((Class<? extends PropertyModel>) clazz);
                }
            } catch (ClassNotFoundException e) {
                LOG.warn("Class was not found", e);
            }
        }

        return specificationClasses;
    }

    private Set<Path> getGroovyFiles(Path[] directories) throws IOException {
        Set<Path> files = new HashSet<Path>();

        for (Path directory : directories) {
            if (Files.exists(directory) && Files.isDirectory(directory)) {

                try (Stream<Path> stream = Files.find(directory, Integer.MAX_VALUE, (path, attr) -> {
                    return Files.isRegularFile(path) && path.toString().endsWith(GROOVY_EXTENSION);
                })) {
                    files.addAll(stream.collect(Collectors.toList()));
                }
            }
        }

        return files;
    }

    protected boolean isValidSpecificationClass(Class<?> clazz) {
        return (!Modifier.isAbstract(clazz.getModifiers()))
                && (PropertyModel.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(PropertyModelDescriptor.class));
    }
}
