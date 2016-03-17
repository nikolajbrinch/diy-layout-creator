package org.diylc.specifications.ic

import groovy.transform.EqualsAndHashCode;

import org.diylc.core.components.properties.PropertyModel;
import org.diylc.core.components.properties.PropertyModelDescriptor;
import org.diylc.core.utils.ReflectionUtils

@EqualsAndHashCode(includes = ['id', 'category', 'name'], useCanEqual = true)
@PropertyModelDescriptor(category = 'IC')
public class ICTypePropertyModel implements PropertyModel {

    String id

    String category

    String name

    String packageType

    String description

    Map<String, List<Map<String, Object>>> pins

    int width
    
    int height
    
    @Override
    public String toString() {
        return name
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false
        }

        if (this.is(obj)) {
            return true
        }

        ICTypePropertyModel other = ReflectionUtils.dynamicCast(obj, ICTypePropertyModel)

        if (other == null) {
            return false
        }

        if (id != other.id) {
            return false
        }

        if (category == null) {
            if (other.category != null) {
                return false
            }
        } else if (!category.equals(other.category)) {
            return false
        }

        if (name == null) {
            if (other.name != null) {
                return false
            }
        } else if (!name.equals(other.name)) {
            return false
        }

        return true
    }

}
