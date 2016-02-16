package org.diylc.components;

public enum Angle {
    
    _0(0),
    _45(45.0 * Math.PI / 180.0),
    _90(90.0 * Math.PI / 180.0),
    _135(135.0 * Math.PI / 180.0),
    _180(180.0 * Math.PI / 180.0),
    _225(225.0 * Math.PI / 180.0),
    _270(270.0 * Math.PI / 180.0),
    _315(315.0 * Math.PI / 180.0),
    _360(360.0 * Math.PI / 180.0);
    
    private double angle;
    
    private Angle(double angle) {
        this.angle = angle;
    }
    
    public double getAngle() {
        return angle;
    }
}
