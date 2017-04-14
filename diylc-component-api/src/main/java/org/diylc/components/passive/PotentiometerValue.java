package org.diylc.components.passive;

import org.diylc.components.Taper;
import org.diylc.core.measures.Resistance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PotentiometerValue {

    private Resistance resistance;
    
    private Taper taper;

    public PotentiometerValue(Resistance resistance, Taper taper) {
        this.resistance = resistance;
        this.taper = taper;
    }

    @Override
    public String toString() {
        return resistance.toString() + " " + taper.toString();
    }
}
