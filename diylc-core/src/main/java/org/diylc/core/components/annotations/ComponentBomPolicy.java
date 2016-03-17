package org.diylc.core.components.annotations;

import org.diylc.core.components.BomPolicy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ComponentBomPolicy {

    /**
     * @return controls what should be shown the BOM
     */
    BomPolicy value() default BomPolicy.SHOW_ALL_NAMES;

}
