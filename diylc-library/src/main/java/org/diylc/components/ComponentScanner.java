package org.diylc.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentScanner {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentScanner.class);

    public Set<String> getClasses(ClassLoader loader, String packageName) throws IOException {
        List<String> resources = getResources(loader, packageName);

        Set<String> classNames = new HashSet<String>();

        if (resources != null) {
            for (String filename : resources) {
                if ((filename.indexOf("!") > 0) & (filename.indexOf(".jar") > 0)) {
                    String jarPath = filename.substring(0, filename.indexOf("!")).substring(filename.indexOf(":") + 1);
                    if (jarPath.indexOf(":") >= 0) {
                        jarPath = jarPath.substring(1);
                    }
                    classNames.addAll(getClassesInJar(jarPath, packageName.replace('.', '/')));
                } else {
                    classNames.addAll(getClassesInDirectory(new File(filename), packageName));
                }
            }
        }

        return classNames;
    }

    private List<String> getResources(ClassLoader loader, String packageName) throws IOException {
        List<String> filenames = new ArrayList<>();

        String path = packageName.replace('.', '/');

        Enumeration<URL> resources = loader.getResources(path);

        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            if (url != null) {
                String filename = URLDecoder.decode(url.getFile(), Charset.defaultCharset().name());
                LOG.debug("Found resource \"" + filename + "\"");
                filenames.add(filename);
            }
        }

        return filenames;
    }

    private Set<String> getClassesInDirectory(File directory, String packageName) throws IOException {
        Set<String> classNames = new HashSet<String>();

        if (directory.exists()) {
            String directoryName = directory.getCanonicalPath();
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    classNames.addAll(getClassesInDirectory(file, packageName + "." + file.getName()));
                } else {
                    String filename = file.getName();

                    if (filename.endsWith(".class")) {
                        String className = packageName + '.' + stripFilenameExtension(filename);
                        LOG.debug("Found class \"" + className + "\" in \"" + directoryName + "\"");
                        classNames.add(className);
                    }
                }
            }
        }

        return classNames;
    }

    private String stripFilenameExtension(String file) {
        int i = file.length() - 1;
        while (file.charAt(i) != '.' && i > 0)
            i--;
        return file.substring(0, i);
    }

    private Set<String> getClassesInJar(String jar, String packageName) throws FileNotFoundException, IOException {
        Set<String> classNames = new HashSet<String>();

        JarInputStream jarFile = null;

        try {
            jarFile = new JarInputStream(new FileInputStream(jar));
            JarEntry jarEntry;
            do {
                jarEntry = jarFile.getNextJarEntry();
                if (jarEntry != null) {
                    String className = jarEntry.getName();
                    if (className.endsWith(".class")) {
                        className = stripFilenameExtension(className);
                        if (className.startsWith(packageName)) {
                            classNames.add(className.replace('/', '.'));
                        }
                    }
                }
            } while (jarEntry != null);
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    /*
                     * Ignore
                     */
                }
            }
        }

        return classNames;
    }
}
