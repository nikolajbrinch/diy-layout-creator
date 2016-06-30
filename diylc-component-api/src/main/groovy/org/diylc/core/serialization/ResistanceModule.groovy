package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.measures.Resistance

@CompileStatic
class ResistanceModule extends AbstractModule<Resistance> {
    
    public ResistanceModule() {
        super('ResistanceModule', Resistance.class, new ResistanceDeserializer(), new ResistanceSerializer())
    }

}
