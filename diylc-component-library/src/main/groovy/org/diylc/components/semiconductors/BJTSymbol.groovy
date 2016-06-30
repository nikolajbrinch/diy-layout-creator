package org.diylc.components.semiconductors

import org.diylc.components.Colors

import java.awt.Polygon
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.GeneralPath

import org.diylc.components.AbstractTransistorSymbol
import org.diylc.components.BJTPolarity
import org.diylc.core.ComponentDescriptor;
import org.diylc.core.IDIYComponent
import org.diylc.core.ObjectCache;
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext

@ComponentDescriptor(name = "BJT Symbol", author = "Branislav Stojkovic", category = "Schematics", instanceNamePrefix = "Q", description = "Bipolar junction transistor schematic symbol", stretchable = false, zOrder = IDIYComponent.COMPONENT, rotatable = false)
public class BJTSymbol extends AbstractTransistorSymbol {

    public static final String id = "a0d13aaa-69cc-43dd-9814-a3bdc1d8b0be"
    
	private static final long serialVersionUID = 1L

	@EditableProperty(name = "Polarity")
	BJTPolarity polarity = BJTPolarity.NPN

	protected Shape[] getBody() {
		if (this.@body == null) {
			this.@body = new Shape[3]
			int x = controlPoints[0].x
			int y = controlPoints[0].y
			int pinSpacing = (int) PIN_SPACING.convertToPixels()

			GeneralPath polyline = new GeneralPath()

			polyline.moveTo((double) x + pinSpacing / 2, (double) y - pinSpacing)
			polyline.lineTo((double) x + pinSpacing / 2, (double) y + pinSpacing)

			this.@body[0] = polyline

			polyline = new GeneralPath()

			polyline.moveTo((double) x, (double) y)
			polyline.lineTo((double) x + pinSpacing / 2, (double) y)
			polyline.moveTo((double) x + pinSpacing / 2, (double) y - pinSpacing / 2)
			polyline.lineTo((double) x + pinSpacing * 2, (double) y - pinSpacing)
			polyline.lineTo((double) x + pinSpacing * 2, (double) y - pinSpacing * 2)
			polyline.moveTo((double) x + pinSpacing / 2, (double) y + pinSpacing / 2)
			polyline.lineTo((double) x + pinSpacing * 2, (double) y + pinSpacing)
			polyline.lineTo((double) x + pinSpacing * 2, (double) y + pinSpacing * 2)
			this.@body[1] = polyline

			Area arrow
			double theta
			if (polarity == BJTPolarity.NPN) {
				theta = Math.atan(1.0 / 3)
				arrow = new Area(new Polygon([ x + pinSpacing,
						x + pinSpacing, x + pinSpacing * 10 / 6 ] as int[],
						[ y - pinSpacing / 5 + pinSpacing / 2,
								y + pinSpacing / 5 + pinSpacing / 2,
								y + pinSpacing / 2 ] as int[], 3))
				arrow.transform(AffineTransform.getRotateInstance(theta, x
						+ pinSpacing / 2, y + pinSpacing / 2))
			} else {
				theta = -Math.atan(1.0 / 3)
				arrow = new Area(new Polygon([x + pinSpacing,
						x + pinSpacing * 10 / 6, x + pinSpacing * 10 / 6 ] as int[],
						[ y - pinSpacing / 2,
								y - pinSpacing / 5 - pinSpacing / 2,
								y + pinSpacing / 5 - pinSpacing / 2 ] as int[], 3))
				arrow.transform(AffineTransform.getRotateInstance(theta, x
						+ pinSpacing / 2, y - pinSpacing / 2))
			}
			this.@body[2] = arrow
		}
        
		return this.@body
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		graphicsContext.setColor(Colors.SCHEMATIC_COLOR)

		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(2))
		graphicsContext.drawLine(width / 3, height / 5, width / 3, height * 4 / 5)

		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		graphicsContext.drawLine(width / 8, height / 2, width / 3, height / 2)

		graphicsContext.drawLine(width * 3 / 4, 1, width * 3 / 4, height / 4)
		graphicsContext.drawLine(width / 3, height / 3 + 1, width * 3 / 4, height / 4)

		graphicsContext.drawLine(width * 3 / 4, height - 1, width * 3 / 4, height * 3 / 4)
		graphicsContext.drawLine(width / 3, height * 2 / 3 - 1, width * 3 / 4,
				height * 3 / 4)
	}

	public void setPolarity(BJTPolarity polarity) {
		this.polarity = polarity
		// Invalidate body
		body = null
	}
}
