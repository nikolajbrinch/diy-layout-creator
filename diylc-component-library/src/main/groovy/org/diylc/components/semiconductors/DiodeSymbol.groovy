package org.diylc.components.semiconductors

import org.diylc.components.Colors

import java.awt.Polygon

import org.diylc.components.AbstractDiodeSymbol
import org.diylc.core.ComponentDescriptor;
import org.diylc.core.IDIYComponent
import org.diylc.core.ObjectCache;
import org.diylc.core.components.CreationMethod;
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentDescriptor(name = "Diode (schematic symbol)", author = "Branislav Stojkovic", category = "Schematics", creationMethod = CreationMethod.POINT_BY_POINT, instanceNamePrefix = "D", description = "Diode schematic symbol", zOrder = IDIYComponent.COMPONENT)
public class DiodeSymbol extends AbstractDiodeSymbol {

    public static final String id = "5bb24cb5-09a9-4681-9a9f-0b3db6d482b2"
    
	private static final long serialVersionUID = 1L

	private static Size BAND_SIZE = new Size(0.01, SizeUnit.in)

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		int size = width * 3 / 8
		int bandSize = 1
		graphicsContext.rotate(-Math.PI / 4, width / 2, height / 2)
		graphicsContext.setColor(Colors.SCHEMATIC_LEAD_COLOR)
		graphicsContext.drawLine(0, height / 2, (width - size) / 2, height / 2)
		graphicsContext.drawLine((int) (width + size / Math.sqrt(2) + bandSize) / 2, height / 2, width, height / 2)
		graphicsContext.setColor(Colors.SCHEMATIC_COLOR)
		graphicsContext.fill(new Polygon([ (width - size) / 2, (width - size) / 2, (int) ((width - size) / 2 + size / Math.sqrt(2)) ] as int[], [(height - size) / 2, (height + size) / 2, height / 2 ] as int[], 3))
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(bandSize))
		graphicsContext.drawLine((int) ((width - size) / 2 + size / Math.sqrt(2)), (height - size) / 2, (int) ((width - size) / 2 + size / Math.sqrt(2)), (height + size) / 2)
	}

	@Override
	protected void decorateComponentBody(GraphicsContext graphicsContext, boolean outlineMode) {
		double width = getWidth().convertToPixels()
		int bandSize = (int) BAND_SIZE.convertToPixels()
		graphicsContext.setColor(getBodyColor())
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(bandSize))
		graphicsContext.drawLine((int) (width / Math.sqrt(2)) + bandSize, 0, (int) (width / Math.sqrt(2) + bandSize), (int) width)
	}
}
