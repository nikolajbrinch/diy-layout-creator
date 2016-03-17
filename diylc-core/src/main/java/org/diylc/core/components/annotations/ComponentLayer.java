package org.diylc.core.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.diylc.core.components.IDIYComponent;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ComponentLayer {

    /**
     * @return Layer of the component.
     */
    double value() default IDIYComponent.COMPONENT;

    /**
     * @return true if the component may go beyond it's predefined layer. In
     *         that case, <code>layer</code> is used as initial z order of the
     *         component.
     */
    boolean flexible() default false;

}
