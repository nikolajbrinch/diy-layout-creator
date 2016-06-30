package org.diylc.core.components;

import org.diylc.core.CreationMethod;
import org.diylc.core.IDIYComponent;
import org.diylc.core.annotations.BomPolicy;

import java.io.Serializable;

import javax.swing.*;

/**
 * Created by neko on 18/03/16.
 */
public interface ComponentModel extends Serializable {
    
    public String getId();

    public String getName();

    public String getDescription();

    public CreationMethod getCreationMethod();

    public String getCategory();

    public String getNamePrefix();

    public String getAuthor();

    public Icon getIcon();

    public void setIcon(Icon icon);

    public Class<? extends IDIYComponent> getInstanceClass();

    public double getzOrder();

    public boolean isFlexibleZOrder();

    public boolean isStretchable();

    public BomPolicy getBomPolicy();

    public boolean isAutoEdit();

    public boolean isRotatable();
    
}
