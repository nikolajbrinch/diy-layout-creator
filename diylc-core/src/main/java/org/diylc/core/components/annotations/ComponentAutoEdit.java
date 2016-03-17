package org.diylc.core.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ComponentAutoEdit {

    /**
     * @return when true, component editor dialog should be shown in Auto-Edit
     *         mode.
     */
    boolean value() default true;

}
