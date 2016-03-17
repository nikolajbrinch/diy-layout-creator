package org.diylc.core.components.properties;

import org.diylc.core.ValidationException;

/**
 * Validator that allows all values.
 * 
 * @author Branislav Stojkovic
 */
public class AllowAllValidator implements IPropertyValidator {

	@Override
	public void validate(Object value) throws ValidationException {
	}
}
