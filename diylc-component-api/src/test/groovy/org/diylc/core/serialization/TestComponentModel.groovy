package org.diylc.core.serialization

import javax.swing.Icon

import org.diylc.core.IDIYComponent
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.components.ComponentModel
import org.diylc.core.components.CreationMethod

class TestComponentModel implements ComponentModel {

    String id

    Class<?> instanceClass
    
    @Override
    public String getId() {
        return id
    }

    @Override
    public String getName() {
        return null
    }

    @Override
    public String getDescription() {
        return null
    }

    @Override
    public CreationMethod getCreationMethod() {
        return null
    }

    @Override
    public String getCategory() {
        return null
    }

    @Override
    public String getNamePrefix() {
        return null
    }

    @Override
    public String getAuthor() {
        return null
    }

    @Override
    public Icon getIcon() {
        return null
    }

    @Override
    public void setIcon(Icon icon) {
    }

    @Override
    public Class<? extends IDIYComponent> getInstanceClass() {
        return instanceClass
    }

    @Override
    public double getzOrder() {
        return 0
    }

    @Override
    public boolean isFlexibleZOrder() {
        return false
    }

    @Override
    public boolean isStretchable() {
        return false
    }

    @Override
    public BomPolicy getBomPolicy() {
        return null
    }

    @Override
    public boolean isAutoEdit() {
        return false
    }

    @Override
    public boolean isRotatable() {
        return false
    }

}
