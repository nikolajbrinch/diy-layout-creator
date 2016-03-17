package org.diylc.core.components.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to denote a Property model. A Property model is a class that
 * implements PropertyModel, and has a property specification category associated, so that
 * all property specifications of that category is enumerated as the specific type of
 * PropertyModel.
 * I.e. all property specifications with category = "IC" will be of type ICTypePropertyModel.
 * 
 * @author neko
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyModelDescriptor {

    /**
     * The specification category.
     * 
     * @return
     */
    public String category() default "";

}
