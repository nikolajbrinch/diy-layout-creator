package org.diylc.components.connectivity

import org.diylc.components.Colors

import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.CubicCurve2D

import org.diylc.components.AbstractCurvedComponent
import org.diylc.components.ComponentDescriptor
import org.diylc.components.Geometry;
import org.diylc.components.PCBLayer
import org.diylc.core.ComponentState
import org.diylc.core.IDIYComponent
import org.diylc.core.ObjectCache;
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@ComponentDescriptor(name = "Curved Trace", author = "Branislav Stojkovic", category = "Connectivity", instanceNamePrefix = "Trace", description = "Curved copper trace with two control points", zOrder = IDIYComponent.TRACE, bomPolicy = BomPolicy.NEVER_SHOW, autoEdit = false)
public class CurvedTrace extends AbstractCurvedComponent<Void> implements Geometry {

    private static final long serialVersionUID = 1L

    public static Color COLOR = Color.black
    public static Size SIZE = new Size(1d, SizeUnit.mm)

    @XStreamAlias("size")
    @EditableProperty(name = "Width")
    Size thickness = SIZE

    @EditableProperty
    PCBLayer layer = PCBLayer._1

    @Override
    protected Color getDefaultColor() {
        return COLOR
    }
    
    Void value = null

    @Override
    protected void drawCurve(CubicCurve2D curve, GraphicsContext graphicsContext, ComponentState componentState) {
        int thickness = toInt(thickness.convertToPixels())
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(thickness))
        Color curveColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : color
        graphicsContext.setColor(curveColor)
        graphicsContext.draw(curve)
    }

    @Override
    public Byte getAlpha() {
        return super.getAlpha()
    }

    @Override
    public void setAlpha(Byte alpha) {
        super.setAlpha(alpha)
    }
}
