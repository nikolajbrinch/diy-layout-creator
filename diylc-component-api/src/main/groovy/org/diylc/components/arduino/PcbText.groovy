package org.diylc.components.arduino

class PcbText {

    String text
    
    Placement placement

    public PcbText(String text, Placement placement) {
        this.text = text
        this.placement = placement
    }    
    
    enum Placement {
        ABOVE,
        BELOW,
        RIGHT,
        LEFT
    }
}
