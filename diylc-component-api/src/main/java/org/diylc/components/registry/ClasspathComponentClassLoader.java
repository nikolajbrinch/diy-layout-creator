package org.diylc.components.registry;

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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.diylc.core.EventType;
import org.diylc.core.IDIYComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClasspathComponentClassLoader extends AbstractComponentClassLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ClasspathComponentClassLoader.class);

    @SuppressWarnings("unchecked")
    Collection<? extends Class<? extends IDIYComponent>> loadCompiledClasses(ClassLoader classLoader) {
        Set<Class<? extends IDIYComponent>> componentClasses = new HashSet<Class<? extends IDIYComponent>>();

        Set<String> componentTypeClasses = null;

        try {
            componentTypeClasses = getClasses(classLoader, COMPONENT_PACKAGE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        double count = componentTypeClasses.size();
        double counter = 1;
        for (String className : componentTypeClasses) {
            getEventDispatcher().sendEvent(EventType.SPLASH_UPDATE, "Loading component class: " + Math.round(counter / count * 100d) + "%");
            LOG.debug("Loading component class: \"" + className + "\"");
            try {
                Class<?> clazz = Class.forName(className);

                if (isValidComponentClass(clazz)) {
                    componentClasses.add((Class<? extends IDIYComponent>) clazz);
                }
            } catch (ClassNotFoundException e) {
                LOG.warn("Class was not found", e);
            }
            counter++;
        }

        return componentClasses;
    }
    
    public Set<String> getClasses(ClassLoader loader, String packageName) throws IOException {
        Set<String> classNames = new HashSet<String>();

        List<String> resources = getResources(loader, packageName);

        if (resources != null) {
            for (String filename : resources) {
                if (isJarFile(filename)) {
                    String jarPath = retrieveJarPath(filename);
                    
                    classNames.addAll(getClassesInJar(Paths.get(jarPath), packageName));
                } else {
                    classNames.addAll(getClassesInDirectory(Paths.get(filename), packageName));
                }
            }
        }

        return classNames;
    }

    private List<String> getResources(ClassLoader loader, String packageName) throws IOException {
        List<String> filenames = new ArrayList<>();

        String packageNamePath = packageName.replace('.', '/');

        for (URL resource : Collections.list(loader.getResources(packageNamePath))) {
            if (resource != null) {
                String filename = decodeFilename(resource);
                
                LOG.debug("Found resource \"" + filename + "\"");
                filenames.add(filename);
            }
        }

        return filenames;
    }


    private Set<String> getClassesInDirectory(Path directory, String packageName) throws IOException {
        Set<String> classNames = new HashSet<String>();

            if (Files.exists(directory) && Files.isDirectory(directory)) {

                try (Stream<Path> stream = Files.find(directory,Integer.MAX_VALUE, (path, attr) -> {
                            File file = path.toFile();
                            String filename = file.getName();
                            return Files.isRegularFile(path) && filename.endsWith(CLASS_EXTENSION);   
                        })) {
                    classNames.addAll(stream.map((path) -> {
                        File file = path.toFile();
                        String filename = file.getName();
                        return packageName + '.' + filename.substring(filename.lastIndexOf('.'));
                    }).collect(Collectors.toList()));
                }
        }

        return classNames;
    }

    private Set<String> getClassesInJar(Path path, String packageName) throws FileNotFoundException, IOException {
        Set<String> classNames = new HashSet<String>();

        String packageNamePath = packageName.replace('.', '/');
        
        try (JarInputStream jarInputStream = new JarInputStream(Files.newInputStream(path))) {
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            
            while (jarEntry != null) {
                
                if (jarEntry != null) {
                    String filename = jarEntry.getName();
                    
                    if (filename.endsWith(CLASS_EXTENSION)) {
                        filename = filename.substring(filename.lastIndexOf('.'));
                        
                        if (filename.startsWith(packageNamePath)) {
                            classNames.add(filename.replace('/', '.'));
                        }
                    }
                }
                
                jarEntry = jarInputStream.getNextJarEntry();
            } 
        } 

        return classNames;
    }

    private String decodeFilename(URL url) throws UnsupportedEncodingException {
        return URLDecoder.decode(url.getFile(), Charset.defaultCharset().name());
    }

    private boolean isJarFile(String filename) {
        return (filename.indexOf("!") > 0) & (filename.indexOf(JAR_EXTENSION) > 0);
    }

    private String retrieveJarPath(String filename) {
        String jarPath = filename.substring(0, filename.indexOf("!")).substring(filename.indexOf(":") + 1);
        
        if (jarPath.indexOf(":") >= 0) {
            jarPath = jarPath.substring(1);
        }
        
        return jarPath;
    }
}
