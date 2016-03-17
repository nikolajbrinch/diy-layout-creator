package org.diylc.components.semiconductors

import org.diylc.components.AbstractDiodeSymbol
import org.diylc.components.Colors
import org.diylc.components.Geometry
import org.diylc.core.components.annotations.ComponentAutoEdit;
import org.diylc.core.components.annotations.ComponentCreationMethod;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.annotations.ComponentPads;
import org.diylc.core.components.CreationMethod
import org.diylc.core.components.IDIYComponent
import org.diylc.core.ObjectCache;
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

import java.awt.*

@ComponentAutoEdit
@ComponentPads(false)
@ComponentCreationMethod(CreationMethod.POINT_BY_POINT)
@ComponentDescriptor(name = "Zener diode (schematic symbol)", author = "Branislav Stojkovic", category = "Schematics", instanceNamePrefix = "D", description = "Zener diode schematic symbol")
public class ZenerDiodeSymbol extends AbstractDiodeSymbol implements Geometry{

    public static final String id = "0f9d914c-f6ba-40b3-a3c7-5982903825ba"
    
    private static final long serialVersionUID = 1L

    private static Size BAND_SIZE = new Size(0.01, SizeUnit.in)

    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int size = width * 3 / 8
        int bandSize = 1
        graphicsContext.with {
            rotate(-Math.PI / 4, width / 2, height / 2)
            setColor(Colors.SCHEMATIC_LEAD_COLOR)
            drawLine(0, height / 2, (width - size) / 2, height / 2)
            drawLine((int) (width + size / Math.sqrt(2) + bandSize) / 2,
                    height / 2, width, height / 2)
            setColor(Colors.SCHEMATIC_COLOR)
            fill(new Polygon([(width - size) / 2,
                                              (width - size) / 2,
                                              (int) ((width - size) / 2 + size / Math.sqrt(2))] as int[], [
                    (height - size) / 2, (height + size) / 2, height / 2] as int[], 3))
            setStroke(ObjectCache.getInstance().fetchBasicStroke(bandSize))
            drawLine((int) ((width - size) / 2 + size / Math.sqrt(2)),
                    (height - size) / 2, (int) ((width - size) / 2 + size / Math.sqrt(2)), (height + size) / 2)
            int finSize = 2 * width / 32
            drawLine((int) ((width - size) / 2 + size / Math.sqrt(2)),
                    (height - size) / 2, (int) ((width - size) / 2 + size / Math.sqrt(2) + finSize), (height - size) / 2
                    - finSize)
            drawLine((int) ((width - size) / 2 + size / Math.sqrt(2)),
                    (height + size) / 2, (int) ((width - size) / 2 + size / Math.sqrt(2) - finSize), (height + size) / 2
                    + finSize)
        }
    }

    @Override
    protected void decorateComponentBody(GraphicsContext graphicsContext, boolean outlineMode) {
        double width = getWidth().convertToPixels()
        double finSize = width / 5
        int bandSize = (int) BAND_SIZE.convertToPixels()

        graphicsContext.with {
            setColor(getBodyColor())
            setStroke(ObjectCache.getInstance().fetchBasicStroke(bandSize))
            drawPolyline([
                (int) (width / Math.sqrt(2) + bandSize + finSize),
                (int) (width / Math.sqrt(2) + bandSize),
                (int) (width / Math.sqrt(2) + bandSize),
                (int) (width / Math.sqrt(2) + bandSize - finSize)] as int[], [
                (int) -finSize, 0, (int) width, (int) (width + finSize)] as int[], 4)
        }
    }
}
