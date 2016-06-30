package org.diylc.application

import groovy.transform.CompileStatic

import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component


@Component
@Lazy
@CompileStatic
@Scope(value = "window")
class DiylcModel {

    DiylcModel() {
        println this
    }
}
