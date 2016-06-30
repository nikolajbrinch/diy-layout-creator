package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.IDIYComponent
import org.diylc.core.components.registry.ComponentFactory;
import org.diylc.core.components.registry.ComponentRegistry

@CompileStatic
class ComponentModule extends AbstractModule<IDIYComponent>{

    public ComponentModule(ComponentRegistry componentRegistry, ComponentFactory componentFactory) {
        super('ComponentModule', IDIYComponent.class, new ComponentDeserializer(componentRegistry, componentFactory), null)
    }
    
}
