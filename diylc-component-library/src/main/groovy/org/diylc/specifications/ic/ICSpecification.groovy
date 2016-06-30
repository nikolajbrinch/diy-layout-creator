package org.diylc.specifications.ic

import groovy.transform.EqualsAndHashCode;

import org.diylc.core.utils.ReflectionUtils
import org.diylc.specifications.Specification
import org.diylc.specifications.SpecificationDescriptor

@EqualsAndHashCode(includes = ['id', 'category', 'name'], useCanEqual = true)
@SpecificationDescriptor(category = 'IC')
public class ICSpecification implements Specification {

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

        ICSpecification other = ReflectionUtils.dynamicCast(obj, ICSpecification)

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
