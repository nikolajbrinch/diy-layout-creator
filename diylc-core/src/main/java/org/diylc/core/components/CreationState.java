package org.diylc.core.components;

import java.awt.Point;
import java.util.List;

public class CreationState {

    private ComponentModel componentModelSlot;

    private Template template;

    private List<IDIYComponent> componentSlot;

    private Point firstControlPoint;

    private Point potentialControlPoint;

    public CreationState() {
    }

    public CreationState(ComponentModel componentModelSlot, Point firstControlPoint) {
        this(componentModelSlot, null, firstControlPoint);
    }

    public CreationState(ComponentModel componentModelSlot, Template template, Point firstControlPoint) {
        this.componentModelSlot = componentModelSlot;
        this.template = template;
        this.firstControlPoint = firstControlPoint;
    }

    public ComponentModel getComponentModelSlot() {
        return componentModelSlot;
    }

    public Template getTemplate() {
        return template;
    }

    public List<IDIYComponent> getComponentSlot() {
        return componentSlot;
    }

    public Point getFirstControlPoint() {
        return firstControlPoint;
    }

    public Point getPotentialControlPoint() {
        return potentialControlPoint;
    }

    public void setComponentModelSlot(ComponentModel componentModelSlot) {
        this.componentModelSlot = componentModelSlot;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public void setComponentSlot(List<IDIYComponent> componentSlot) {
        this.componentSlot = componentSlot;
    }

    public void setFirstControlPoint(Point firstControlPoint) {
        this.firstControlPoint = firstControlPoint;
    }

    public void setPotentialControlPoint(Point potentialControlPoint) {
        this.potentialControlPoint = potentialControlPoint;
    }

}
