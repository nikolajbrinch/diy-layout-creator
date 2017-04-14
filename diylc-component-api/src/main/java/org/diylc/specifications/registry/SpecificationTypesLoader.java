package org.diylc.specifications.registry;

import groovy.lang.GroovyClassLoader;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codehaus.groovy.control.CompilationFailedException;
import org.diylc.core.Resource;
import org.diylc.core.ResourceLoader;
import org.diylc.specifications.Specification;
import org.diylc.specifications.SpecificationDescriptor;
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

        Set<Class<? extends Specification>> specificationTypes = findSpecificationTypes(classLoader, directories);

        for (Class<? extends Specification> specificationType : specificationTypes) {
            if (specificationType.isAnnotationPresent(SpecificationDescriptor.class)) {
                SpecificationDescriptor annotation = specificationType.getAnnotation(SpecificationDescriptor.class);
                String category = annotation.category();
                specificationTypeRegistry.add(category, specificationType);
            }
        }

        return specificationTypeRegistry;
    }

    private Set<Class<? extends Specification>> findSpecificationTypes(ClassLoader classLoader, Path[] directories) throws IOException {
        Set<Class<? extends Specification>> specificationClasses = new HashSet<Class<? extends Specification>>();

        if (directories.length > 0) {
            specificationClasses.addAll(findGroovySpecificationTypes(classLoader, directories));
        }

        if (specificationClasses.isEmpty()) {
            specificationClasses.addAll(findClasspathSpecificationTypes(classLoader));
        }

        return specificationClasses;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Specification> loadGroovySpecificationType(GroovyClassLoader groovyClassLoader, Path path) {
        Class<? extends Specification> specificationClass = null;
        LOG.trace("Loading specification file: \"" + path.toAbsolutePath().toString() + "\"");
        try {
            Class<?> clazz = groovyClassLoader.parseClass(path.toFile());

            if (isValidSpecificationClass(clazz)) {
                specificationClass = (Class<? extends Specification>) clazz;
            }
        } catch (CompilationFailedException e) {
            LOG.warn("Compilation failed", e);
        } catch (IOException e) {
            LOG.warn("IOException loading class", e);
        }

        return specificationClass;

    }

    private Set<Class<? extends Specification>> findGroovySpecificationTypes(ClassLoader classLoader, Path[] directories) {
        Set<Class<? extends Specification>> specificationClasses = new HashSet<Class<? extends Specification>>();

        try (GroovyClassLoader groovyClassLoader = new GroovyClassLoader(classLoader)) {
            for (Path directory : directories) {
                groovyClassLoader.addClasspath(directory.toString());
            }

            Set<Path> specificationTypeFiles = null;

            try {
                specificationTypeFiles = getGroovyFiles(directories);
            } catch (Exception e) {
                LOG.error("Error getting groovy files", e);
            }

            specificationClasses = specificationTypeFiles.parallelStream()
                    .map((tf) -> loadGroovySpecificationType(groovyClassLoader, tf))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            LOG.warn("IOException closing GroovyClassLoader", e);
        }

        return specificationClasses;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Specification> instantiateClass(Resource resource) {
        Class<? extends Specification> clazz = null;

        try {
            LOG.trace("Loading specification class: \"" + resource + "\"");

            String filename = resource.getFilename();
            clazz = (Class<? extends Specification>) Class.forName(filename.substring(0, filename.lastIndexOf('.')).replace('/', '.'));
        } catch (ClassNotFoundException e) {
            LOG.warn("Class was not found", e);
        }

        return clazz;
    }

    private Set<Class<? extends Specification>> findClasspathSpecificationTypes(ClassLoader classLoader) throws IOException {
        Set<Resource> classResources = resourceLoader.getResources(classLoader, SPECIFICATION_PACKAGE_NAME, CLASS_EXTENSION);

        return classResources.parallelStream().map((resource) -> instantiateClass(resource)).filter(Objects::nonNull)
                .filter((clazz) -> isValidSpecificationClass(clazz)).collect(Collectors.toSet());
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
                && (Specification.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(SpecificationDescriptor.class));
    }
}
