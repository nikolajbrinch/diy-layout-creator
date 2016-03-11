package org.diylc.core;

import java.lang.reflect.InvocationTargetException;

/**
 * Entity class for editable properties extracted from component objects.
 * Represents a single editable property together with it's current value.
 * 
 * @author Branislav Stojkovic
 */
public class PropertyWrapper implements Cloneable {

    private final String name;

    private final Class<?> type;

    private final String setter;

    private final String getter;

    private final boolean defaultable;

    private final IPropertyValidator validator;

    private final String targetName;

    private Object value;

    private boolean unique = true;

    private boolean changed = false;

    public PropertyWrapper(String name, String targetName, Class<?> type, String getter, String setter, boolean defaultable,
            IPropertyValidator validator) {
        super();
        this.name = name;
        this.targetName = targetName;
        this.type = type;
        this.getter = getter;
        this.setter = setter;
        this.defaultable = defaultable;
        this.validator = validator;
    }

    public void readFrom(Object object) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
            SecurityException, NoSuchMethodException {
        Object target = getTarget(object); 
        this.value = target.getClass().getMethod(getGetter()).invoke(target);
    }

    public void writeTo(Object object) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
            SecurityException, NoSuchMethodException {
        Object target = getTarget(object); 
        target.getClass().getMethod(getSetter(), type).invoke(target, this.value);
    }

    public String getName() {
        return name;
    }

    public String getTargetName() {
        return targetName;
    }

    public Class<?> getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isDefaultable() {
        return defaultable;
    }

    public IPropertyValidator getValidator() {
        return validator;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        PropertyWrapper clone = new PropertyWrapper(this.name, this.targetName, this.type, this.getGetter(), this.getSetter(), this.defaultable,
                this.validator);
        clone.value = this.value;
        clone.changed = this.changed;
        clone.unique = this.unique;

        return clone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + (defaultable ? 1231 : 1237);
        result = prime * result + ((getGetter() == null) ? 0 : getGetter().hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((getSetter() == null) ? 0 : getSetter().hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        PropertyWrapper other = (PropertyWrapper) obj;

        if (defaultable != other.defaultable) {
            return false;
        }

        if (getGetter() == null) {
            if (other.getGetter() != null) {
                return false;
            }
        } else if (!getGetter().equals(other.getGetter())) {
            return false;
        }

        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        if (getSetter() == null) {
            if (other.getSetter() != null) {
                return false;
            }
        } else if (!getSetter().equals(other.getSetter())) {
            return false;
        }

        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }

    private Object getTarget(Object object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Object target = object;
        
        if (targetName != null) {
            target = object.getClass().getMethod("get" + targetName.substring(0, 1).toUpperCase() + targetName.substring(1)).invoke(object);
        }
        
        return target;
    }

    public String getGetter() {
        return getter;
    }

    public String getSetter() {
        return setter;
    }

}
