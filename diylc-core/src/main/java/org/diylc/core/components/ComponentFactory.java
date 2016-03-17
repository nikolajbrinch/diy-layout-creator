package org.diylc.core.components;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.diylc.core.Project;
import org.diylc.core.config.Configuration;
import org.diylc.core.components.properties.PropertyApplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentFactory.class);

    public static int MAX_RECENT_COMPONENTS = 16;

    private final PropertyApplier propertyApplier;
    
    public ComponentFactory(PropertyApplier propertyApplier) {
        this.propertyApplier = propertyApplier;
    }
    
    public CreationState createComponent(CreationState creationState, Project project)
            throws Exception {
        
        ComponentModel componentModel = creationState.getComponentModelSlot();
        LOG.trace("Instatiating component of type: " + componentModel.getComponentClass().getName());

        /*
         * Instantiate the component.
         */
        IDIYComponent component = componentModel.getComponentClass().newInstance();
        component.setComponentModel(componentModel);

        component.setName(ComponentNameCreator.createComponentName(componentModel, project));

        /* 
         * Translate them to the desired location.
         */
        Point point = creationState.getFirstControlPoint();
        
        if (point != null) {
            for (int j = 0; j < component.getControlPointCount(); j++) {
                Point controlPoint = new Point(component.getControlPoint(j));
                controlPoint.translate(point.x, point.y);
                component.setControlPoint(controlPoint, j);
            }
        }
        
        Template template = creationState.getTemplate();

        propertyApplier.applyTemplateControlPoints(component, template);
        propertyApplier.applyDefaultProperties(component, template);

        /* 
         * Write to recent components
         */
        List<String> recentComponentTypes = Configuration.INSTANCE.getRecentComponents();
        String componentTypeId = componentModel.getComponentId();
        if (recentComponentTypes.size() == 0 || !recentComponentTypes.get(0).equals(componentTypeId)) {
            /*
             * Remove if it's already somewhere in the list.
             */
            recentComponentTypes.remove(componentTypeId);
            /*
             * Add to the end of the list.
             */
            recentComponentTypes.add(0, componentTypeId);
            /*
             * Trim the list if necessary.
             */
            if (recentComponentTypes.size() > MAX_RECENT_COMPONENTS) {
                recentComponentTypes.remove(recentComponentTypes.size() - 1);
            }
            Configuration.INSTANCE.setRecentComponents(recentComponentTypes);
        }

        List<IDIYComponent> list = new ArrayList<IDIYComponent>();
        list.add(component);

        creationState.setComponentSlot(list);
        
        return creationState;
    }
    
    public CreationState createComponentPointByPoint(CreationState creationState, Point scaledPoint, Project project) throws Exception {
        creationState.setFirstControlPoint(scaledPoint);
        creationState = createComponent(creationState, project);

        /*
         * Set the other control point to the same location, we'll move it later
         * when mouse moves.
         */
        creationState.getComponentSlot().get(0).setControlPoint(creationState.getFirstControlPoint(), 0);
        creationState.getComponentSlot().get(0).setControlPoint(creationState.getFirstControlPoint(), 1);
        
        return creationState;
    }
}
