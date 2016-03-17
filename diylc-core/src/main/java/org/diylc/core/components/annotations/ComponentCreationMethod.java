package org.diylc.core.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.diylc.core.components.CreationMethod;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ComponentCreationMethod {
    
    /**
     * @return method that should be used to create a component. If
     *         <code>CreationMethod.POINT_BY_POINT</code> is used, user will
     *         have to select ending points before the component is created.
     */
    CreationMethod value() default CreationMethod.SINGLE_CLICK;

}
