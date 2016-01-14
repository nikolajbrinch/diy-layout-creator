package org.diylc.components.passive

import org.diylc.components.Taper
import org.diylc.core.measures.Resistance

public class PotentiometerValue {

	Resistance resistance
	Taper taper

	public PotentiometerValue(Resistance resistance, Taper taper) {
		this.resistance = resistance
		this.taper = taper
	}

	@Override
	public String toString() {
		return resistance.toString() + " " + taper.toString()
	}
}
