package org.diylc.core.measures;


public enum InductanceUnit implements Unit {

	pH(1, "pH"), nH(1e3, "nH"), uH(1e6, "\u039CH"), mH(1e9, "mH"), H(1e12, "H");

	double factor;
	
	String display;

	private InductanceUnit(double factor, String display) {
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
	
    public static InductanceUnit fromString(String unit) {
        InductanceUnit foundUnit = null;
        
        for (InductanceUnit inductanceUnit : InductanceUnit.values()) {
            if (inductanceUnit.display.equals(unit)) {
                foundUnit = inductanceUnit;
            }
        }
        
        return foundUnit;
    }
	
}
