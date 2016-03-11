package org.diylc.specifications;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to denote a Specification. A Specification is a class that
 * implements Specification, and has a specification category associated, so that
 * all specifications of that category is enumerated as the specific type of
 * Specification.
 * I.e. all specifications with category = "IC" will be of type ICSpecification.
 * 
 * @author neko
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpecificationDescriptor {

    /**
     * The specification category.
     * 
     * @return
     */
    public String category() default "";

}
