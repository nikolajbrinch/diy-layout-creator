package org.diylc.components;

import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;

public class Constants {

    public enum Placement {
        ABOVE,
        BELOW,
        RIGHT,
        LEFT
    }

    public static Size SMALL_PAD_SIZE = new Size(0.07d, SizeUnit.in);
    
    public static Size LARGE_HOLE_SIZE = new Size(1.33d, SizeUnit.mm);
    

}
