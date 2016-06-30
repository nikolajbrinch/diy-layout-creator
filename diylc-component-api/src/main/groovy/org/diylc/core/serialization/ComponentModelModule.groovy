package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.components.ComponentModel
import org.diylc.core.components.registry.ComponentRegistry

@CompileStatic
class ComponentModelModule extends AbstractModule<ComponentModel> {

    public ComponentModelModule(ComponentRegistry componentRegistry) {
        super('ComponentModelModule', ComponentModel.class, new ComponentModelDeserializer(componentRegistry), new ComponentModelSerializer())
    }
}
