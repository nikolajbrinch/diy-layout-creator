package org.diylc.common;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.diylc.core.IDIYComponent;
import org.diylc.core.VisibilityPolicy;


/**
 * Entity bean that represents a control point. Has methods for reading/writing
 * {@link Point} value from/to a component.
 * 
 * @author Branislav Stojkovic
 */
public class ControlPointWrapper {

	private String name;
	private Point value;
	private Method setter;
	private Method getter;
	private boolean editable;
	private boolean sticky;
	private VisibilityPolicy visibilityPolicy;

	public ControlPointWrapper(String name, Method getter, Method setter, boolean editable,
			boolean sticky, VisibilityPolicy visibilityPolicy) {
		super();
		this.name = name;
		this.getter = getter;
		this.setter = setter;
		this.editable = editable;
		this.sticky = sticky;
		this.visibilityPolicy = visibilityPolicy;
	}

	/**
	 * Reads value from the specified component by invoking it's
	 * <code>getter</code> method.
	 * 
	 * @param component
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void readFrom(IDIYComponent component) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		value = (Point) getter.invoke(component);
	}

	/**
	 * Writes the value into the specified component by invoking it's
	 * <code>setter</code> method.
	 * 
	 * @param component
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void writeTo(IDIYComponent component) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		setter.invoke(component, value);
	}

	public String getName() {
		return name;
	}

	public boolean isEditable() {
		return editable;
	}

	public boolean isSticky() {
		return sticky;
	}

	public Point getValue() {
		return value;
	}

	public void setValue(Point value) {
		this.value = value;
	}

	public VisibilityPolicy getVisibilityPolicy() {
		return visibilityPolicy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ControlPointWrapper other = (ControlPointWrapper) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
