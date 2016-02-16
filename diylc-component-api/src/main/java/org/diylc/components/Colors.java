package org.diylc.components;

import java.awt.Color;

public class Colors {

    public static Color PCB_BOARD_COLOR = Color.decode("#F8EBB3");

    public static Color PCB_BLUE_COLOR = new Color(61, 89, 171);

    public static Color PCB_GREEN_COLOR = new Color(58, 102, 41);

    public static Color PCB_BORDER_COLOR = PCB_BOARD_COLOR.darker();

    public static Color PCB_BLUE_BORDER_COLOR = PCB_BLUE_COLOR.darker();

    public static Color PCB_GREEN_BORDER_COLOR = PCB_GREEN_COLOR.darker();

    public static Color PCB_STRIP_COLOR = Color.decode("#DA8A67");

    public static Color EYELET_BOARD_COLOR = Color.decode("#CCFFCC");
    
    public static Color EYELET_BORDER_COLOR = EYELET_BOARD_COLOR.darker();
    
    public static Color EYELET_COLOR = Color.decode("#C3E4ED");

    public static Color MARSHALL_BOARD_COLOR = Color.decode("#CD8500");
    
    public static Color MARSHALL_BORDER_COLOR = MARSHALL_BOARD_COLOR.darker();

    public static Color COORDINATE_COLOR = Color.gray.brighter();

    public static Color SELECTION_COLOR = Color.red;
    
    public static Color LABEL_COLOR = Color.black;
    
    public static Color LABEL_COLOR_SELECTED = Color.red;
    
    public static Color METAL_COLOR = Color.decode("#236B8E");
    
    public static Color GUIDELINE_COLOR = Color.blue;
    
    public static Color DEFAULT_LEAD_COLOR = Color.decode("#CCCCCC");
    
    public static Color SCHEMATIC_LEAD_COLOR = Color.black;

    public static Color SCHEMATIC_COLOR = Color.blue;

    public static Color LEAD_COLOR_ICON = DEFAULT_LEAD_COLOR.darker().darker();

    public static Color SHAPE_BORDER_COLOR = Color.black;

    public static Color SHAPE_FILL_COLOR = Color.white;

    public static Color TRANSISTOR_COLOR = Color.black;

    public static Color TUBE_COLOR = Color.black;
    
    public static Color BREADBOARD_HOLE_COLOR = Color.decode("#EEEEEE");

    public static Color PLUS_COLOR = Color.red;
    
    public static Color MINUS_COLOR = Color.blue;
    
    public static byte MAX_ALPHA = 127;

    public static Color SILVER_COLOR = Color.decode("#C0C0C0");

    public static Color CHIP_COLOR = Color.gray;

    public static Color CHIP_BORDER_COLOR = Color.gray.darker();

}
