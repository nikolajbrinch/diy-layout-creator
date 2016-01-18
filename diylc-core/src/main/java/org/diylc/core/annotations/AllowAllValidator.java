package org.diylc.core.annotations;

import org.diylc.core.IPropertyValidator;
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
