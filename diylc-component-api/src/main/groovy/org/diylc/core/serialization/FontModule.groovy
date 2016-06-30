package org.diylc.core.serialization

import java.awt.Font

import groovy.transform.CompileStatic;

@CompileStatic
class FontModule extends AbstractModule<Font> {
    
    public FontModule() {
        super('FontModule', Font.class, new FontDeserializer(), new FontSerializer())
    }

}
