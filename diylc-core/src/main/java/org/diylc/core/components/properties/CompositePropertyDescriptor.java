package org.diylc.core.components.properties;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A special PropertyWrapper, that wraps a Specification Property (a
 * Specification Model), and contatins all the nedsted PropertyWrappers for the
 * properties of the Specification Model.
 * 
 * @author neko
 */
public class CompositePropertyDescriptor extends PropertyDescriptor {

    private List<PropertyModel> propertyModels = new ArrayList<>();

    private List<PropertyDescriptor> properties = new ArrayList<>();

    private Class<? extends PropertyModel> specificationType;

    private Class<? extends PropertyModelEditor> specificationEditor;

    public CompositePropertyDescriptor(PropertyDescriptor propertyWrapper) {
        super(propertyWrapper.getName(), propertyWrapper.getTargetName(), propertyWrapper.getType(), propertyWrapper.getGetter(),
                propertyWrapper.getSetter(), propertyWrapper.isDefaultable(), propertyWrapper.getValidator());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        CompositePropertyDescriptor clone = new CompositePropertyDescriptor((PropertyDescriptor) super.clone());
        clone.setPropertyModels(new LinkedList<>(this.getPropertyModels()));
        clone.setProperties(PropertyUtils.cloneProperties(this.getProperties()));
        clone.setSpecificationType(this.getSpecificationType());
        clone.setSpecificationEditor(this.getSpecificationEditor());
        
        return clone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;

        int result = super.hashCode();
        result = prime * result + ((getProperties() == null) ? 0 : getProperties().hashCode());
        result = prime * result + ((getPropertyModels() == null) ? 0 : getPropertyModels().hashCode());
        result = prime * result + ((getSpecificationType() == null) ? 0 : getSpecificationType().hashCode());
        result = prime * result + ((getSpecificationEditor() == null) ? 0 : getSpecificationEditor().hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);

        if (equals) {
            CompositePropertyDescriptor other = (CompositePropertyDescriptor) obj;

            if (getProperties() == null) {
                if (other.getProperties() != null) {
                    return false;
                }
            } else if (!getProperties().equals(other.getProperties())) {
                return false;
            }

            if (getPropertyModels() == null) {
                if (other.getPropertyModels() != null) {
                    return false;
                }
            } else if (!getPropertyModels().equals(other.getPropertyModels())) {
                return false;
            }

            if (getSpecificationType() == null) {
                if (other.getSpecificationType() != null) {
                    return false;
                }
            } else if (!getSpecificationType().equals(other.getSpecificationType())) {
                return false;
            }

            if (getSpecificationEditor() == null) {
                if (other.getSpecificationEditor() != null) {
                    return false;
                }
            } else if (!getSpecificationEditor().equals(other.getSpecificationEditor())) {
                return false;
            }
        }

        return equals;
    }

    public List<PropertyDescriptor> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyDescriptor> properties) {
        this.properties = properties;
    }

    public List<PropertyModel> getPropertyModels() {
        return propertyModels;
    }

    public void setPropertyModels(List<PropertyModel> propertyModels) {
        this.propertyModels = propertyModels;
    }

    public Class<? extends PropertyModel> getSpecificationType() {
        return specificationType;
    }

    public void setSpecificationType(Class<? extends PropertyModel> specificationType) {
        this.specificationType = specificationType;
    }

    public Class<? extends PropertyModelEditor> getSpecificationEditor() {
        return specificationEditor;
    }

    public void setSpecificationEditor(Class<? extends PropertyModelEditor> specificationEditor) {
        this.specificationEditor = specificationEditor;
    }
}
