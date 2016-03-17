package org.diylc.core.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ComponentEditOptions {

    /**
     * @return when false, moving one control point will cause all the others to
     *         move together with it.
     */
    boolean stretchable() default true;

    /**
     * @return true if component may be rotated, false otherwise
     * @return
     */
    boolean rotatable() default true;

}
