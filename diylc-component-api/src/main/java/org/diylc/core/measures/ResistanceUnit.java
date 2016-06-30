package org.diylc.core.measures;

public enum ResistanceUnit implements Unit {

	R(1, "\u2126"), K(1e3, "K\u2126"), M(1e6, "M\u2126");

	double factor;
	
	String display;

	private ResistanceUnit(double factor, String display) {
		this.factor = factor;
		this.display = display;
	}

	@Override
	public double getFactor() {
		return factor;
	}

	@Override
	public String toString() {
		return display;
	}
	
    public static ResistanceUnit fromString(String unit) {
        ResistanceUnit foundUnit = null;
        
        for (ResistanceUnit resistanceUnit : ResistanceUnit.values()) {
            if (resistanceUnit.display.equals(unit)) {
                foundUnit = resistanceUnit;
            }
        }
        
        return foundUnit;
    }

}
