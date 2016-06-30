package org.diylc.application

import groovy.transform.CompileStatic

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@CompileStatic
@Component
class WindowManager {

    @Autowired
    ApplicationWindowFactory windowFactory

    static Set<WindowContext> windowContexts = new HashSet<>()

    public static WindowContext getWindowContext() {
        return windowContexts.getAt(0)
    }

    public DiylcFrame newWindow() {
        windowContexts.add(new WindowContext())

        DiylcFrame window = windowFactory.newWindow()

        return window
    }
}
