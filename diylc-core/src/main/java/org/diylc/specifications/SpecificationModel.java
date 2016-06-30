package org.diylc.specifications;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation declaring a property of a component for a Specification Model
 * Specification models are used to encapsulate properties, that can be edited
 * using a Specification Editor, which is a controller that is used by
 * the properties editing framework.
 * 
 * @author neko
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpecificationModel {

    /**
     * The category of specifications this Model belongs to
     * 
     * @return
     */
    public String category() default "";

    /**
     * The specification type, which is a class that implements Specification.
     * 
     * @return
     */
    Class<? extends Specification> type() default Specification.class;
    
    /**
     * The Specification Editor, which is a class that implements SpecificationEditor.
     * 
     * @return
     */
    Class<? extends SpecificationEditor> editor() default SpecificationEditor.class;
    
}
