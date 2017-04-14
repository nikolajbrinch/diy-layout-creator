package org.diylc.specifications.registry;

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
import org.diylc.core.Resource;
import org.diylc.core.ResourceLoader;
import org.diylc.specifications.Specification;
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
        LOG.debug("Loading specifications.");
        SpecificationRegistry specificationRegistry = new SpecificationRegistry();

        Set<URL> specifications = findSpecifications(classLoader, directories);

        specifications.parallelStream().forEach((url) -> {
            LOG.trace("Loading specification " + url);
            try (InputStream inputStream = url.openStream()) {
                Specification specification = new SpecificationReader(specificationTypeRegistry).read(inputStream);
                specificationRegistry.add(specification);
                progressView.update("Loading specifications: " + specification.getCategory() + "." + specification.getName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

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

                try (Stream<Path> stream = Files.find(directory, Integer.MAX_VALUE, (path, attr) -> {
                    return Files.isRegularFile(path) && path.toString().endsWith(SPECIFICATION_EXTENSION);
                })) {
                    files.addAll(stream.parallel().map(path -> {
                        URL url = null;
                        try {
                            url = path.toFile().toURI().toURL();
                        } catch (IOException e) {
                        }
                        return url;
                    }).collect(Collectors.toList()));

                }
            }
        }

        return files;
    }

    private Set<URL> findClasspathSpecifications(ClassLoader classLoader) throws IOException {
        Set<Resource> classResources = resourceLoader.getResources(classLoader, "", SPECIFICATION_EXTENSION);

        return classResources.parallelStream().map((cr) -> {
            LOG.trace("Adding " + cr + " from classpath to specification files");
            URL url = null;

            try {
                url = cr.toUrl();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return url;
        }).collect(Collectors.toSet());
    }
}
