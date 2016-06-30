package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.measures.Power

@CompileStatic
class PowerModule extends AbstractModule<Power> {
    
    public PowerModule() {
        super('PowerModule', Power.class, new PowerDeserializer(), new PowerSerializer())
    }

}
