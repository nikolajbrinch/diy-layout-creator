package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.measures.Inductance

@CompileStatic
class InductanceModule extends AbstractModule<Inductance> {
    
    public InductanceModule() {
        super('InductanceModule', Inductance.class, new InductanceDeserializer(), new InductanceSerializer())
    }

}
