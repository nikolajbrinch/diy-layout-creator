package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.measures.Voltage
import org.diylc.core.serialization.AbstractModule

@CompileStatic
class VoltageModule extends AbstractModule<Voltage> {
    
    public VoltageModule() {
        super('VoltageModule', Voltage.class, new VoltageDeserializer(), new VoltageSerializer())
    }

}
