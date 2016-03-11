package org.diylc.core;

import javax.swing.Icon;

import org.diylc.core.annotations.BomPolicy;

/**
 * Entity class used to describe a component type.
 * 
 * @author Branislav Stojkovic
 * 
 * @see IDIYComponent
 */
public class ComponentType {

	private final String name;
	
	private final String description;
	
	private final CreationMethod creationMethod;
	
	private final String category;
	
	private final String namePrefix;
	
	private final String author;
	
	private final Class<? extends IDIYComponent> instanceClass;
	
	private final double zOrder;
	
	private final boolean flexibleZOrder;
	
	private final boolean stretchable;
	
	private final BomPolicy bomPolicy;
	
	private final boolean autoEdit;
	
	private final boolean rotatable;
	
    private Icon icon;
	
	public ComponentType(
	        String name, 
	        String description, 
	        CreationMethod creationMethod,
			String category, 
			String namePrefix, 
			String author, 
			Icon icon,
			Class<? extends IDIYComponent> instanceClass, 
			double zOrder, 
			boolean flexibleZOrder,
			boolean stretchable, 
			BomPolicy bomPolicy, 
			boolean autoEdit, 
			boolean rotatable) {
		this.name = name;
		this.description = description;
		this.creationMethod = creationMethod;
		this.category = category;
		this.namePrefix = namePrefix;
		this.author = author;
		this.icon = icon;
		this.instanceClass = instanceClass;
		this.zOrder = zOrder;
		this.flexibleZOrder = flexibleZOrder;
		this.stretchable = stretchable;
		this.bomPolicy = bomPolicy;
		this.autoEdit = autoEdit;
		this.rotatable = rotatable;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public CreationMethod getCreationMethod() {
		return creationMethod;
	}

	public String getCategory() {
		return category;
	}

	public String getNamePrefix() {
		return namePrefix;
	}

	public String getAuthor() {
		return author;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
	    this.icon = icon;
    }

	public Class<? extends IDIYComponent> getInstanceClass() {
		return instanceClass;
	}

	public double getZOrder() {
		return zOrder;
	}
	
	public boolean isFlexibleZOrder() {
		return flexibleZOrder;
	}

	public boolean isStretchable() {
		return stretchable;
	}
	
	public BomPolicy getBomPolicy() {
		return bomPolicy;
	}
	
	public boolean isAutoEdit() {
		return autoEdit;
	}
	
	public boolean isRotatable() {
		return rotatable;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
