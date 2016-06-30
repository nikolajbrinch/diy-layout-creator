package org.diylc.core.components

import groovy.transform.CompileStatic

import javax.swing.Icon

import org.diylc.core.IDIYComponent
import org.diylc.core.annotations.BomPolicy

/**
 * Entity class used to describe a component type.
 * 
 * @author Branislav Stojkovic
 * 
 * @see IDIYComponent
 */
@CompileStatic
public class DefaultComponentModel implements ComponentModel {

    String id

    String name

    String description

    CreationMethod creationMethod

    String category

    String namePrefix

    String author

    Class<? extends IDIYComponent> instanceClass

    double zOrder

    boolean flexibleZOrder

    boolean stretchable

    BomPolicy bomPolicy

    boolean autoEdit

    boolean rotatable

    Icon icon

    public DefaultComponentModel() {
        
    }
        
    public DefaultComponentModel(
    String id,
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
        this.id = id
        this.name = name
        this.description = description
        this.creationMethod = creationMethod
        this.category = category
        this.namePrefix = namePrefix
        this.author = author
        this.icon = icon
        this.instanceClass = instanceClass
        this.zOrder = zOrder
        this.flexibleZOrder = flexibleZOrder
        this.stretchable = stretchable
        this.bomPolicy = bomPolicy
        this.autoEdit = autoEdit
        this.rotatable = rotatable
    }

}
