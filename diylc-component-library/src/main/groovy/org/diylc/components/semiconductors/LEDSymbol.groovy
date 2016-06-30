package org.diylc.components.semiconductors

import java.awt.Polygon
import java.awt.geom.AffineTransform

import org.diylc.core.ComponentDescriptor;
import org.diylc.core.IDIYComponent
import org.diylc.core.ObjectCache;
import org.diylc.core.components.CreationMethod;
import org.diylc.core.graphics.GraphicsContext

@ComponentDescriptor(name = "LED (schematic symbol)", author = "Branislav Stojkovic", category = "Schematics", creationMethod = CreationMethod.POINT_BY_POINT, instanceNamePrefix = "D", description = "Diode schematic symbol", zOrder = IDIYComponent.COMPONENT)
public class LEDSymbol extends DiodeSymbol {

    public static final String id = "7cf93d8d-bdc5-4078-9dbb-94d340fac3de"
    
	private static final long serialVersionUID = 1L

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		AffineTransform tx = graphicsContext.getTransform()
		super.drawIcon(graphicsContext, width, height)
		int arrowSize = 3 * width / 32
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		// Go back to original transform
		graphicsContext.setTransform(tx)
		// g2d.drawLine((width - size) / 2 + size / 8, (height + size) / 2,
		// (width - size) / 2 + size
		// / 8 + arrowSize, (height + size) / 2 + arrowSize);
		graphicsContext
				.drawLine(width * 9 / 16, height * 10 / 16, width * 9 / 16 + arrowSize,
						height * 10 / 16)
		graphicsContext
				.drawLine(width * 9 / 16, height * 11 / 16, width * 9 / 16 + arrowSize,
						height * 11 / 16)
	}

	@Override
	protected void decorateComponentBody(GraphicsContext graphicsContext, boolean outlineMode) {
		super.decorateComponentBody(graphicsContext, outlineMode)

		// Draw arrows
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		double width = getWidth().convertToPixels()
		double arrowLength = width / 3
		double arrowSize = width / 6
		int d = (int) (width / 3)

		int x2 = (int) (d / 2 + Math.cos(Math.PI / 4) * arrowLength)
		int y2 = (int) (width + Math.sin(Math.PI / 4) * arrowLength)
		graphicsContext.drawLine(d / 2, (int) width, x2 - 2, y2 - 2)
		graphicsContext.fillPolygon(new Polygon([ x2, x2, (int) (x2 - arrowSize) ] as int[],  [ y2,
				(int) (y2 - arrowSize), y2 ]as int[], 3))

		x2 = (int) (3 * d / 2 + Math.cos(Math.PI / 4) * arrowLength)
		y2 = (int) (width + Math.sin(Math.PI / 4) * arrowLength)
		graphicsContext.drawLine(3 * d / 2, (int) width, x2 - 2, y2 - 2)
		graphicsContext.fillPolygon(new Polygon([ x2, x2, (int) (x2 - arrowSize) ] as int[], [ y2,
				(int) (y2 - arrowSize), y2 ] as int[], 3))
	}
}
