package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.measures.Current
import org.diylc.core.measures.Voltage

@CompileStatic
class CurrentModule extends AbstractModule<Current> {
    
    public CurrentModule() {
        super('CurrentModule', Current.class, new CurrentDeserializer(), new CurrentSerializer())
    }

}
