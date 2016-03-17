package org.diylc.core.components.registry;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.diylc.core.ProgressView;
import org.diylc.core.components.properties.PropertyModel;
import org.diylc.core.resources.Resource;
import org.diylc.core.resources.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpecificationsLoader {

    private static final Logger LOG = LoggerFactory.getLogger(SpecificationsLoader.class);

    public static final String SPECIFICATION_EXTENSION = ".spec";

    private ResourceLoader resourceLoader = new ResourceLoader();

    public SpecificationsLoader() {
    }

    public SpecificationRegistry loadSpecifications(SpecificationTypeRegistry specificationTypeRegistry, Path[] directories,
            ProgressView progressView) throws IOException {
        return loadSpecifications(specificationTypeRegistry, null, directories, progressView);
    }

    public SpecificationRegistry loadSpecifications(SpecificationTypeRegistry specificationTypeRegistry, ClassLoader classLoader,
            ProgressView progressView) throws IOException {
        return loadSpecifications(specificationTypeRegistry, classLoader, null, progressView);
    }

    public SpecificationRegistry loadSpecifications(SpecificationTypeRegistry specificationTypeRegistry, ClassLoader classLoader,
            Path[] directories, ProgressView progressView) throws IOException {
        LOG.info("Loading specifications.");
        SpecificationRegistry specificationRegistry = new SpecificationRegistry();

        Set<URL> specifications = findSpecifications(classLoader, directories);

        for (URL url : specifications) {
            LOG.debug("Loading specification " + url);
            try (InputStream inputStream = url.openStream()) {
                PropertyModel propertyModel = new SpecificationReader(specificationTypeRegistry).read(inputStream);
                specificationRegistry.add(propertyModel);
                progressView.update("Loading specifications: " + propertyModel.getCategory() + "." + propertyModel.getName());
            }
        }

        return specificationRegistry;
    }

    private Set<URL> findSpecifications(ClassLoader classLoader, Path[] directories) throws IOException {
        Set<URL> specifications = new HashSet<URL>();

        if (classLoader != null) {
            specifications.addAll(findClasspathSpecifications(classLoader));
        }
        if (directories != null) {
            specifications.addAll(findSpecificationFiles(directories));
        }

        return specifications;
    }

    private Set<URL> findSpecificationFiles(Path[] directories) throws IOException {
        Set<URL> files = new HashSet<URL>();

        for (Path directory : directories) {
            if (Files.exists(directory) && Files.isDirectory(directory)) {

                try (Stream<Path> stream = Files.find(directory,Integer.MAX_VALUE,
                        (path, attr) -> {
                            return Files.isRegularFile(path) && path.toString().endsWith(SPECIFICATION_EXTENSION);   
                        })) {
                    files.addAll(stream.map(path -> { URL url = null;
                        try { url = path.toFile().toURI().toURL(); } catch (Exception e) {} return url; }
                    ).collect(Collectors.toList()));
                    
                }
            }
        }
        
        return files;
    }

    private Set<URL> findClasspathSpecifications(ClassLoader classLoader) throws IOException {
        Set<URL> files = new HashSet<URL>();

        Set<Resource> classResources = resourceLoader.getResources(classLoader, "", SPECIFICATION_EXTENSION);

        for (Resource resource : classResources) {
            LOG.debug("Adding " + resource + " from classpath to specification files");
            files.add(resource.toUrl());
        }

        return files;
    }
}
