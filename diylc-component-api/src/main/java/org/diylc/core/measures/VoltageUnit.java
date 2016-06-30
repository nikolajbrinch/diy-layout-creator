package org.diylc.core.measures;


public enum VoltageUnit implements Unit {

	mV(1e-3, "mV"), V(1, "V"), KV(1e3, "KV");

	double factor;
	String display;

	private VoltageUnit(double factor, String display) {
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
	
    public static VoltageUnit fromString(String unit) {
        VoltageUnit foundUnit = null;
        
        for (VoltageUnit voltageUnit : VoltageUnit.values()) {
            if (voltageUnit.display.equals(unit)) {
                foundUnit = voltageUnit;
            }
        }
        
        return foundUnit;
    }
	
}
