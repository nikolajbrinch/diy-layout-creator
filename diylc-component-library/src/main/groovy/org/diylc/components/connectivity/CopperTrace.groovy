package org.diylc.components.connectivity

import org.diylc.components.Colors

import java.awt.Color
import java.awt.Shape

import org.diylc.components.AbstractLeadedComponent
import org.diylc.components.PCBLayer
import org.diylc.core.components.annotations.ComponentBomPolicy;
import org.diylc.core.components.annotations.ComponentCreationMethod;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.annotations.ComponentLayer;
import org.diylc.core.components.ComponentState
import org.diylc.core.components.CreationMethod
import org.diylc.core.components.IDIYComponent
import org.diylc.core.ObjectCache
import org.diylc.core.components.VisibilityPolicy
import org.diylc.core.components.BomPolicy
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentBomPolicy(BomPolicy.NEVER_SHOW)
@ComponentLayer(IDIYComponent.TRACE)
@ComponentCreationMethod(CreationMethod.POINT_BY_POINT)
@ComponentDescriptor(name = "Copper Trace", author = "Branislav Stojkovic", category = "Connectivity", instanceNamePrefix = "Trace", description = "Straight copper trace")
public class CopperTrace extends AbstractLeadedComponent {

    public static final String id = "d8063ff1-b2de-4bdf-9818-6cd0a472094b"

    private static final long serialVersionUID = 1L

    private static Size THICKNESS = new Size(1d, SizeUnit.mm)

    private static Color COLOR = Color.black

    @EditableProperty(name = "Width")
    Size thickness = THICKNESS

    @EditableProperty
    PCBLayer layer = PCBLayer._1

    public CopperTrace() {
        super()
        this.leadColor = COLOR
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(3))
        graphicsContext.setColor(COLOR)
        graphicsContext.drawLine(1, height - 2, width - 2, 1)
    }

    @Override
    protected Color getLeadColorForPainting(ComponentState componentState) {
        return componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : getLeadColor()
    }

    @Override
    public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
        return VisibilityPolicy.WHEN_SELECTED
    }

    @Override
    @EditableProperty(name = "Color")
    public Color getLeadColor() {
        return this.@leadColor
    }

    @Override
    protected int getLeadThickness() {
        return (int) getThickness().convertToPixels()
    }

    @Override
    protected boolean shouldShadeLeads() {
        return false
    }

    @Override
    protected Shape getBodyShape() {
        return null
    }

    @Override
    protected Size getDefaultWidth() {
        return null
    }

    @Override
    protected Size getDefaultLength() {
        return null
    }
}
