package org.diylc.core.serialization

import java.awt.Color

import groovy.transform.CompileStatic;

@CompileStatic
class ColorModule extends AbstractModule<Color> {

    public ColorModule() {
        super('ColorModule', Color.class, new ColorDeserializer(), new ColorSerializer())
    }
}
