package org.diylc.core.measures;

import org.diylc.core.measures.AbstractMeasure;

public class Power extends AbstractMeasure<PowerUnit> {

	private static final long serialVersionUID = 1L;

	public Power(Double value, PowerUnit unit) {
		super(value, unit);
	}

	@Override
	public Power clone() throws CloneNotSupportedException {
		return new Power(value, unit);
	}

	public static Power parseCapacitance(String value) {
		for (PowerUnit unit : PowerUnit.values()) {
			if (value.toLowerCase().endsWith(unit.toString().toLowerCase())) {
				value = value.substring(0, value.length() - unit.toString().length() - 1).trim();
				return new Power(Double.parseDouble(value), unit);
			}
		}
		throw new IllegalArgumentException("Could not parse power: " + value);
	}
}
