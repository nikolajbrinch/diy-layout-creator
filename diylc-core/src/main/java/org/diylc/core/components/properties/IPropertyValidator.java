package org.diylc.core.components.properties;

import org.diylc.core.ValidationException;

/**
 * Interface for validating property value changes.
 * 
 * @author Branislav Stojkovic
 */
public interface IPropertyValidator {

	/**
	 * @throws ValidationException
	 *             if validation fails. Message will contain the reason.
	 */
	void validate(Object value) throws ValidationException;
}
