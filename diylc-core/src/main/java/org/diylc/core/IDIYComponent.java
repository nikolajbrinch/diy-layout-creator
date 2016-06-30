package org.diylc.core;

import java.awt.Point;
import java.io.Serializable;

import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.components.ComponentModel;
import org.diylc.core.components.ComponentState;
import org.diylc.core.graphics.GraphicsContext;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Interface for component instance. Implementation classes of this interface
 * will be instantiated by the application when component is added to the
 * canvas. <br>
 * <br>
 * <b>Implementing classes should meet the following: </b>
 * <ul>
 * <li>Must have an empty constructor.</li>
 * <li>Class should be annotated with {@link ComponentDescriptor}.</li>
 * <li>Getters for properties editable by users should be annotated with
 * {@link EditableProperty}.</li>
 * <li>Component configuration should be stored int <code>public static</code>
 * fields so they can be set through config file.</li>
 * </ul>
 * 
 * @author Branislav Stojkovic
 * 
 * @param <T>
 *            type of component values, e.g. Resistance for resistors or String
 *            for transistors.
 */
// @JsonTypeInfo( use = Id.CLASS )
public interface IDIYComponent extends Serializable {

    public static final double CHASSIS = 1.0;

    public static final double BOARD = 2.0;

    public static final double TRACE_CUT = 3.0;

    public static final double TRACE = 3.0;

    public static final double PAD = 3.1;

    public static final double COMPONENT = 4.0;

    public static final double TEXT = 5.0;

    /**
     * @return component unique instance id.
     */
    public String getId();

    /**
     * @return component instance name.
     */
    public String getName();

    /**
     * Updates component instance name.
     */
    public void setName(String name);

    /**
     * Updates component type.
     */
    public void setComponentModel(ComponentModel componentModel);

    /**
     * @return component type.
     */
    public ComponentModel getComponentModel();

    /**
     * @return number of control points for this component instance. May vary
     *         between two instances of the same type, e.g. DIL IC with 8 and 16
     *         pins will have 8 or 16 pins although they are of the same type.
     */
    @JsonIgnore
    public int getControlPointCount();

    /**
     * @return control point at the specified index.
     */
    public Point getControlPoint(int index);

    /**
     * Updates the control point at the specified index.
     */
    public void setControlPoint(Point point, int index);

    /**
     * @return true, if the specified control point may stick to control points
     *         of other components.
     */
    public boolean isControlPointSticky(int index);

    /**
     * @return true, if the specified control point may overlap with other
     *         control points <b>of the same component</b>. The other control
     *         point must be able to overlap too.
     */
    public boolean canControlPointOverlap(int index);

    public VisibilityPolicy getControlPointVisibilityPolicy(int index);

    /**
     * Draws the component onto the {@link GraphicsContext}.
     */
    public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode, Project project,
            IDrawingObserver drawingObserver);

    /**
     * Draws icon representation of the component. This should not depend on
     * component state, i.e. it should be treated as a static method.
     */
    public void drawIcon(GraphicsContext graphicsContext, int width, int height);

    /**
     * Clones the component.
     */
//    public IDIYComponent clone() throws CloneNotSupportedException;

    /**
     * @return full value for BOM.
     */
    @JsonIgnore
    public String getValueForDisplay();

    /**
     * Checks if two components are equal.
     */
    public boolean equalsTo(IDIYComponent other);

}
