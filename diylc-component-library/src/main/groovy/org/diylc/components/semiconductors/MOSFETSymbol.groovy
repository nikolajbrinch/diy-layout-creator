package org.diylc.components.semiconductors

import org.diylc.components.Colors

import java.awt.Polygon
import java.awt.Shape
import java.awt.geom.GeneralPath

import org.diylc.components.AbstractTransistorSymbol
import org.diylc.components.ComponentDescriptor
import org.diylc.components.FetPolarity
import org.diylc.core.IDIYComponent
import org.diylc.core.ObjectCache;
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext

@ComponentDescriptor(name = "MOSFET Symbol", author = "Branislav Stojkovic", category = "Schematics", instanceNamePrefix = "Q", description = "MOSFET transistor schematic symbol", stretchable = false, zOrder = IDIYComponent.COMPONENT, rotatable = false)
public class MOSFETSymbol extends AbstractTransistorSymbol {

	private static final long serialVersionUID = 1L

	@EditableProperty(name = "Channel")
	FetPolarity polarity = FetPolarity.NEGATIVE

	public Shape[] getBody() {
		if (this.@body == null) {
			this.@body = new Shape[3]
			int x = controlPoints[0].x
			int y = controlPoints[0].y
			int pinSpacing = (int) PIN_SPACING.convertToPixels()

			GeneralPath polyline = new GeneralPath()

			polyline.moveTo((double) x + pinSpacing / 2, (double) y - pinSpacing + 1)
			polyline.lineTo((double) x + pinSpacing / 2, (double) y + pinSpacing - 1)
			polyline.moveTo((double) x + pinSpacing, (double) y - pinSpacing + 1)
			polyline.lineTo((double) x + pinSpacing, (double) y + pinSpacing - 1)
			this.@body[0] = polyline

			polyline = new GeneralPath()

			polyline.moveTo((double) x + pinSpacing, (double) y - pinSpacing)
			polyline.lineTo((double) x + pinSpacing * 2, (double) y - pinSpacing)
			polyline.moveTo((double) x + pinSpacing, (double) y + pinSpacing)
			polyline.lineTo((double) x + pinSpacing * 2, (double) y + pinSpacing)
			polyline.moveTo((double) x, (double) y)
			polyline.lineTo((double) x + pinSpacing / 2, (double) y)
			polyline.moveTo((double) x + pinSpacing * 2, (double) y - pinSpacing * 2)
			polyline.lineTo((double) x + pinSpacing * 2, (double) y - pinSpacing)
			polyline.moveTo((double) x + pinSpacing * 2, (double) y + pinSpacing * 2)
			polyline.lineTo((double) x + pinSpacing * 2, (double) y + pinSpacing)
			this.@body[1] = polyline

			Polygon arrow
			if (polarity == FetPolarity.NEGATIVE) {
				arrow = new Polygon([ x + pinSpacing * 8 / 6, x + pinSpacing * 8 / 6,
						x + pinSpacing * 12 / 6 ] as int[], [ y + pinSpacing * 6 / 5,
						y + pinSpacing * 4 / 5, y + pinSpacing ] as int[], 3)
			} else {
				arrow = new Polygon([ x + pinSpacing * 7 / 6, x + pinSpacing * 11 / 6,
						x + pinSpacing * 11 / 6 ] as int[], [ y - pinSpacing,
						y - pinSpacing * 6 / 5, y - pinSpacing * 4 / 5 ] as int[], 3)
			}
			this.@body[2] = arrow
		}
		return this.@body
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		graphicsContext.setColor(Colors.SCHEMATIC_COLOR)
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(2))
		graphicsContext.drawLine(width * 2 / 5, height / 4 + 1, width * 2 / 5, height * 3 / 4 - 1)
		graphicsContext.drawLine(width * 3 / 5, height / 4 + 1, width * 3 / 5, height * 3 / 4 - 1)

		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		graphicsContext.drawLine(width / 5, height / 2, width * 2 / 5, height / 2)

		graphicsContext.drawLine(width * 4 / 5, 1, width * 4 / 5, height / 4)
		graphicsContext.drawLine(width * 4 / 5, height / 4, width * 3 / 5, height / 4)

		graphicsContext.drawLine(width * 4 / 5, height - 1, width * 4 / 5, height * 3 / 4)
		graphicsContext.drawLine(width * 4 / 5, height * 3 / 4, width * 3 / 5, height * 3 / 4)
	}

	public void setPolarity(FetPolarity polarity) {
		this.@polarity = polarity
		// Invalidate body
		body = null
	}
}
