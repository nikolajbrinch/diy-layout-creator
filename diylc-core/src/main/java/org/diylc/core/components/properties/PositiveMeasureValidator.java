package org.diylc.core.components.properties;

import org.diylc.core.ValidationException;
import org.diylc.core.measures.AbstractMeasure;

public class PositiveMeasureValidator implements IPropertyValidator {

	@Override
	public void validate(Object value) throws ValidationException {
		if (value == null)
			return;
		if (value instanceof AbstractMeasure) {
			AbstractMeasure<?> measure = (AbstractMeasure<?>) value;
			
			if (measure.getValue() < 0) {
				throw new ValidationException("must be greater than zero.");
			}
		} else {
			throw new ValidationException("wrong data type, measure expected.");
		}
	}
}
