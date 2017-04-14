package org.diylc.components.passive;

import org.diylc.core.measures.Resistance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResistorValue {

    private Resistance resistance;

    private Power power;

    public ResistorValue(Resistance resistance, Power power) {
        this.resistance = resistance;
        this.power = power;
    }

    @Override
    public String toString() {
        return resistance.toString() + power.toString();
    }
}
