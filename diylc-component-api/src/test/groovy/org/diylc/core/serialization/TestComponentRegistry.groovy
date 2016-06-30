package org.diylc.core.serialization

import org.diylc.core.IDIYComponent
import org.diylc.core.components.ComponentModel
import org.diylc.core.components.registry.ComponentLookup
import org.diylc.core.components.registry.ComponentModels
import org.diylc.core.components.registry.ComponentRegistry
import org.diylc.specifications.Specification


class TestComponentRegistry implements ComponentRegistry {

    private ComponentLookup componentLookup

    public TestComponentRegistry(ComponentLookup componentLookup) {
        this.componentLookup =  componentLookup
    }

    @Override
    public ComponentModels getComponentModels() {
        return null
    }

    @Override
    public ComponentModel getComponentModel(String id) {
        String className = componentLookup.getComponentClassName(id)
        Class<?> clazz
        
        try {
            clazz = Class.forName(className)
        } catch (Exception e) {
            println "Error instantiating class for id: ${id}"
            throw e
        }
        
        return new TestComponentModel(id: id, instanceClass : clazz)
    }

    @Override
    public Collection<Specification> getSpecifications(String category) {
        return null
    }
}
