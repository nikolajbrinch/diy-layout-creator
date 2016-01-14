package org.diylc.components.passive

import org.diylc.core.measures.Resistance

public class ResistorValue {

	Resistance resistance
	Power power

	public ResistorValue(Resistance resistance, Power power) {
		this.resistance = resistance
		this.power = power
	}

	@Override
	public String toString() {
		return resistance.toString() + power.toString()
	}
}
