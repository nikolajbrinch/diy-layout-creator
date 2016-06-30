package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.measures.Capacitance

@CompileStatic
class CapacitanceModule extends AbstractModule<Capacitance> {
    
    public CapacitanceModule() {
        super('CapacitanceModule', Capacitance.class, new CapacitanceDeserializer(), new CapacitanceSerializer())
    }

}
