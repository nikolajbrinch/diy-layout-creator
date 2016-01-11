package org.diylc.components;

import groovy.lang.GroovyClassLoader;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.codehaus.groovy.control.CompilationFailedException;
import org.diylc.core.IDIYComponent;
import org.diylc.core.graphics.GraphicsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentTypeLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentTypeLoader.class);

    private ComponentScanner componentScanner = new ComponentScanner();

    private ComponentTypeFactory componentTypeFactory = new ComponentTypeFactory();

    Map<String, List<ComponentType>> loadComponentTypes(File[] directories) {
        return loadComponentTypes(Thread.currentThread().getContextClassLoader(), directories);
    }

    private Map<String, List<ComponentType>> loadComponentTypes(ClassLoader classLoader, File[] directories) {
        LOG.info("Loading component types.");

        Set<Class<? extends IDIYComponent<?>>> componentClasses = new TreeSet<Class<? extends IDIYComponent<?>>>(
                new Comparator<Class<? extends IDIYComponent<?>>>() {

                    @Override
                    public int compare(Class<? extends IDIYComponent<?>> o1, Class<? extends IDIYComponent<?>> o2) {
                        return o1.getName().compareTo(o2.getName());
                    }

                });

        // componentClasses.addAll(loadCompiledClasses(classLoader));
        componentClasses.addAll(loadGroovyClasses(classLoader, directories));

        Map<String, List<ComponentType>> componentTypes = new HashMap<String, List<ComponentType>>();

        for (Class<? extends IDIYComponent<?>> componentClass : componentClasses) {
            ComponentType componentType = loadComponentType((Class<? extends IDIYComponent<?>>) componentClass);

            if (componentType != null) {
                List<ComponentType> components = componentTypes.get(componentType.getCategory());

                if (components == null) {
                    components = new ArrayList<ComponentType>();
                    componentTypes.put(componentType.getCategory(), components);
                }

                components.add(componentType);
            }
        }

        return componentTypes;
    }

    @SuppressWarnings("unchecked")
    private Collection<? extends Class<? extends IDIYComponent<?>>> loadCompiledClasses(ClassLoader classLoader) {
        Set<Class<? extends IDIYComponent<?>>> componentClasses = new HashSet<Class<? extends IDIYComponent<?>>>();

        Set<String> componentTypeClasses = null;

        try {
            componentTypeClasses = componentScanner.getClasses(classLoader, "org.diylc.components");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String className : componentTypeClasses) {
            try {
                Class<?> clazz = Class.forName(className);

                if (!Modifier.isAbstract(clazz.getModifiers())) {
                    if (IDIYComponent.class.isAssignableFrom(clazz)) {
                        componentClasses.add((Class<? extends IDIYComponent<?>>) clazz);
                    }
                }
            } catch (ClassNotFoundException e) {
                LOG.warn("Class was not found", e);
            }
        }

        return componentClasses;
    }

    @SuppressWarnings("unchecked")
    private Collection<? extends Class<? extends IDIYComponent<?>>> loadGroovyClasses(ClassLoader classLoader, File[] directories) {
        Set<Class<? extends IDIYComponent<?>>> componentClasses = new HashSet<Class<? extends IDIYComponent<?>>>();

        GroovyClassLoader groovyClassLoader = null;

        try {
            groovyClassLoader = new GroovyClassLoader(classLoader);

            Set<File> componentTypeFiles = null;

            try {
                componentTypeFiles = componentScanner.getGroovyFiles(directories);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (File file : componentTypeFiles) {
                try {
                    Class<?> clazz = groovyClassLoader.parseClass(file);

                    if (!Modifier.isAbstract(clazz.getModifiers())) {
                        if (IDIYComponent.class.isAssignableFrom(clazz)) {
                            componentClasses.add((Class<? extends IDIYComponent<?>>) clazz);
                        }
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

        return componentClasses;
    }

    private ComponentType loadComponentType(Class<? extends IDIYComponent<?>> clazz) {
        ComponentType componentType = null;

        try {
            LOG.debug("Loading \"" + clazz.getName() + "\"");

            IDIYComponent<?> component = (IDIYComponent<?>) clazz.newInstance();

            componentType = componentTypeFactory.newComponentType(component);

            componentType.setIcon(loadIcon(component));

        } catch (InstantiationException | IllegalAccessException e) {
            LOG.warn("Failure loading \"" + clazz.getName() + "\"", e);
        }

        return componentType;
    }

    @SuppressWarnings("unchecked")
    ComponentType loadComponentType(String className) throws ClassNotFoundException {
        return loadComponentType((Class<? extends IDIYComponent<?>>) Class.forName(className));

    }

    private Icon loadIcon(IDIYComponent<?> component) {
        Icon icon = null;

        try {
            Image image = new BufferedImage(Constants.ICON_SIZE, Constants.ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
            GraphicsContext graphicsContext = new GraphicsContext((Graphics2D) image.getGraphics());
            graphicsContext.graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphicsContext.graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            component.drawIcon(graphicsContext, Constants.ICON_SIZE, Constants.ICON_SIZE);
            icon = new ImageIcon(image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return icon;
    }

}
