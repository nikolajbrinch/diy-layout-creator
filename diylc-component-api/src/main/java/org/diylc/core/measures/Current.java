package org.diylc.core.measures;

import org.diylc.core.measures.AbstractMeasure;

public class Current extends AbstractMeasure<CurrentUnit> {

	private static final long serialVersionUID = 1L;

	public Current(Double value, CurrentUnit unit) {
		super(value, unit);
	}

	@Override
	public Current clone() throws CloneNotSupportedException {
		return new Current(value, unit);
	}
}
