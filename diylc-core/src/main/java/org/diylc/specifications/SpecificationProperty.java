package org.diylc.specifications;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.diylc.core.PropertyWrapper;

/**
 * A special PropertyWrapper, that wraps a Specification Property (a
 * Specification Model), and contatins all the nedsted PropertyWrappers for the
 * properties of the Specification Model.
 * 
 * @author neko
 */
public class SpecificationProperty extends PropertyWrapper {

    private List<Specification> specifications = new ArrayList<>();

    private List<PropertyWrapper> properties = new ArrayList<>();

    private Class<? extends Specification> specificationType;

    private Class<? extends SpecificationEditor> specificationEditor;

    public SpecificationProperty(PropertyWrapper propertyWrapper) {
        super(propertyWrapper.getName(), propertyWrapper.getTargetName(), propertyWrapper.getType(), propertyWrapper.getGetter(),
                propertyWrapper.getSetter(), propertyWrapper.isDefaultable(), propertyWrapper.getValidator());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        SpecificationProperty clone = new SpecificationProperty((PropertyWrapper) super.clone());
        clone.setSpecifications(new LinkedList<>(this.getSpecifications()));
        clone.setProperties(new ArrayList<>(this.getProperties()));
        clone.setSpecificationType(this.getSpecificationType());
        clone.setSpecificationEditor(this.getSpecificationEditor());

        return clone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;

        int result = super.hashCode();
        result = prime * result + ((getProperties() == null) ? 0 : getProperties().hashCode());
        result = prime * result + ((getSpecifications() == null) ? 0 : getSpecifications().hashCode());
        result = prime * result + ((getSpecificationType() == null) ? 0 : getSpecificationType().hashCode());
        result = prime * result + ((getSpecificationEditor() == null) ? 0 : getSpecificationEditor().hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);

        if (equals) {
            SpecificationProperty other = (SpecificationProperty) obj;

            if (getProperties() == null) {
                if (other.getProperties() != null) {
                    return false;
                }
            } else if (!getProperties().equals(other.getProperties())) {
                return false;
            }

            if (getSpecifications() == null) {
                if (other.getSpecifications() != null) {
                    return false;
                }
            } else if (!getSpecifications().equals(other.getSpecifications())) {
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

    public List<PropertyWrapper> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyWrapper> properties) {
        this.properties = properties;
    }

    public List<Specification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<Specification> specifications) {
        this.specifications = specifications;
    }

    public Class<? extends Specification> getSpecificationType() {
        return specificationType;
    }

    public void setSpecificationType(Class<? extends Specification> specificationType) {
        this.specificationType = specificationType;
    }

    public Class<? extends SpecificationEditor> getSpecificationEditor() {
        return specificationEditor;
    }

    public void setSpecificationEditor(Class<? extends SpecificationEditor> specificationEditor) {
        this.specificationEditor = specificationEditor;
    }
}
