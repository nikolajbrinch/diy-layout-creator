package org.diylc.core.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.diylc.core.components.IDIYComponent;

/**
 * Annotation for {@link IDIYComponent} implementation. Describes component
 * properties.
 * 
 * @author Branislav Stojkovic
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentDescriptor {

	/**
	 * @return component type name.
	 */
	String name();

	/**
	 * @return component type description.
	 */
	String description();

	/**
	 * @return component category, e.g. "Passive", "Semiconductors", etc.
	 */
	String category();

	/**
	 * @return component author name.
	 */
	String author();

	/**
	 * @return prefix that will be used to generate component instance names,
	 *         e.g. "R" for resistors or "Q" for transistors.
	 */
	String instanceNamePrefix();

}
