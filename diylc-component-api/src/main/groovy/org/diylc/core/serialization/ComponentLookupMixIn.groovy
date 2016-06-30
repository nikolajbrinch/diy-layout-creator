package org.diylc.core.serialization

import groovy.transform.CompileStatic

import org.diylc.core.IDIYComponent

import com.fasterxml.jackson.annotation.JsonIgnore

@CompileStatic
abstract class ComponentLookupMixIn {

    @JsonIgnore
    Map<String, IDIYComponent> componentLookup;
    
}
