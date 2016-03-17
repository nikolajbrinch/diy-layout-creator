package org.diylc.core.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceLoader {

    public static final String JAR_EXTENSION = ".jar";

    public Set<Resource> getResources(String packageName) throws IOException {
        return getResources(packageName, null);
    }

    public Set<Resource> getResources(String packageName, String extension) throws IOException {
        return getResources(Thread.currentThread().getContextClassLoader(), packageName, extension);
    }

    public Set<Resource> getResources(ClassLoader classLoader, String packageName, String extension) throws IOException {
        Set<Resource> resources = new HashSet<Resource>();

        List<String> filenames = getFiles(classLoader, packageName);

        if (filenames != null) {
            for (String filename : filenames) {
                if (isJarFile(filename)) {
                    resources.addAll(getResourcesInJar(retrieveJarPath(filename), packageName, extension));
                } else {
                    resources.addAll(getResourcesInDirectory(Paths.get(filename), packageName, extension));
                }
            }
        }

        return resources;
    }

    private Set<Resource> getResourcesInDirectory(Path directory, String packageName, String extension) throws IOException {
        Set<Resource> resources = new HashSet<Resource>();

        if (Files.exists(directory) && Files.isDirectory(directory)) {

            try (Stream<Path> stream = Files.find(directory, Integer.MAX_VALUE, (path, attr) -> {
                File file = path.toFile();
                String filename = file.getName();
                return Files.isRegularFile(path) && filename.endsWith(extension);
            })) {
                resources.addAll(stream.map((path) -> {
                    String fullPath = path.toAbsolutePath().toString();
                    String filename = packageName.replace('.',  '/') + fullPath.substring(directory.toAbsolutePath().toString().length());
                    return new FileSystemResource(fullPath, filename);
                }).collect(Collectors.toSet()));
            }
        }

        return resources;
    }

    private Set<Resource> getResourcesInJar(Path path, String packageName, String extension) throws FileNotFoundException, IOException {
        Set<Resource> resources = new HashSet<Resource>();

        String packageNamePath = packageName.replace('.', '/');

        try (JarInputStream jarInputStream = new JarInputStream(Files.newInputStream(path))) {
            JarEntry jarEntry = jarInputStream.getNextJarEntry();

            while (jarEntry != null) {

                if (jarEntry != null) {
                    String filename = jarEntry.getName();

                    if (extension != null && extension.length() > 0 && filename.endsWith(extension) || extension == null
                            || extension.length() <= 0) {
                        filename = filename.substring(filename.lastIndexOf('.'));

                        if (filename.startsWith(packageNamePath)) {
                            resources.add(new JarResource(filename));
                        }
                    }
                }

                jarEntry = jarInputStream.getNextJarEntry();
            }
        }

        return resources;
    }

    private List<String> getFiles(ClassLoader loader, String packageName) throws IOException {
        List<String> filenames = new ArrayList<>();

        String packageNamePath = packageName.replace('.', '/');

        for (URL resource : Collections.list(loader.getResources(packageNamePath))) {
            if (resource != null) {
                filenames.add(decodeFilename(resource));
            }
        }

        return filenames;
    }

    private String decodeFilename(URL url) throws UnsupportedEncodingException {
        return URLDecoder.decode(url.getFile(), Charset.defaultCharset().name());
    }

    private boolean isJarFile(String filename) {
        return (filename.indexOf("!") > 0) & (filename.indexOf(JAR_EXTENSION) > 0);
    }

    private Path retrieveJarPath(String filename) {
        String jarPath = filename.substring(0, filename.indexOf("!")).substring(filename.indexOf(":") + 1);

        if (jarPath.indexOf(":") >= 0) {
            jarPath = jarPath.substring(1);
        }

        return Paths.get(jarPath);
    }

}
