package org.diylc.components;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.diylc.common.ComponentType;
import org.diylc.core.IDIYComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentTypeLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentTypeLoader.class);

    private ComponentScanner componentScanner = new ComponentScanner();

    private ComponentTypeFactory componentTypeFactory = new ComponentTypeFactory();

    public Map<String, List<ComponentType>> loadComponentTypes() {
        return loadComponentTypes(Thread.currentThread().getContextClassLoader());
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<ComponentType>> loadComponentTypes(ClassLoader classLoader) {
        LOG.info("Loading component types.");
        Map<String, List<ComponentType>> componentTypes = new HashMap<String, List<ComponentType>>();

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
                        ComponentType componentType = loadComponentType((Class<? extends IDIYComponent<?>>) clazz);

                        if (componentType != null) {
                            List<ComponentType> components = componentTypes.get(componentType.getCategory());

                            if (components == null) {
                                components = new ArrayList<ComponentType>();
                                componentTypes.put(componentType.getCategory(), components);
                            }

                            components.add(componentType);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return componentTypes;
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
    public ComponentType loadComponentType(String className) throws ClassNotFoundException {
        return loadComponentType((Class<? extends IDIYComponent<?>>) Class.forName(className));

    }

    private Icon loadIcon(IDIYComponent<?> component) {
        Icon icon = null;

        try {
            Image image = new BufferedImage(Constants.ICON_SIZE, Constants.ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = (Graphics2D) image.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            component.drawIcon(g2d, Constants.ICON_SIZE, Constants.ICON_SIZE);
            icon = new ImageIcon(image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return icon;
    }

}
