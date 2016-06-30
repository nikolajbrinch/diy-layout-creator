package org.diylc.core.serialization

import org.diylc.core.measures.Size

import groovy.transform.CompileStatic;


@CompileStatic
class SizeModule extends AbstractModule<Size> {

    public SizeModule() {
        super('SizeModule', Size.class, new SizeDeserializer(), new SizeSerializer())
    }
}
