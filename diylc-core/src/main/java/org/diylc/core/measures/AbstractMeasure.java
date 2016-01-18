package org.diylc.core.measures;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.Format;

/***
 * Immutable measure class.
 * 
 * @author bancika
 *
 * @param <T>
 */
public class AbstractMeasure<T extends Enum<? extends Unit>> implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    protected static final Format format = new DecimalFormat("0.####");

    protected final Double value;
    protected final T unit;

    public AbstractMeasure(Double value, T unit) {
        this.value = value;
        this.unit = unit;
    }

    public Double getValue() {
        return value;
    }

    public T getUnit() {
        return unit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        
        int result = 1;
        
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        
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
        
        AbstractMeasure<?> other = (AbstractMeasure<?>) obj;
        
        if (unit == null) {
            if (other.unit != null) {
                return false;
            }
        } else if (!unit.equals(other.unit)) {
            return false;
        }
        
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return format.format(value) + unit;
    }
}
