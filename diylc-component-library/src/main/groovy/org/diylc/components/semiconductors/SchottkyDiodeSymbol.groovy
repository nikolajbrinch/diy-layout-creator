package org.diylc.components.semiconductors;

import java.awt.Polygon;

import org.diylc.common.ObjectCache;
import org.diylc.components.AbstractDiodeSymbol;
import org.diylc.components.ComponentDescriptor;
import org.diylc.core.CreationMethod;
import org.diylc.core.IDIYComponent;
import org.diylc.core.graphics.GraphicsContext;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;

@ComponentDescriptor(name = "Schottky diode (schematic symbol)", author = "Branislav Stojkovic", category = "Schematics", creationMethod = CreationMethod.POINT_BY_POINT, instanceNamePrefix = "D", description = "Schottky diode schematic symbol", zOrder = IDIYComponent.COMPONENT)
public class SchottkyDiodeSymbol extends AbstractDiodeSymbol {

	private static final long serialVersionUID = 1L;

	public static Size BAND_SIZE = new Size(0.01, SizeUnit.in);

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		int size = width * 3 / 8;
		int bandSize = 1;
		graphicsContext.rotate(-Math.PI / 4, width / 2, height / 2);
		graphicsContext.setColor(LEAD_COLOR);
		graphicsContext.drawLine(0, height / 2, (width - size) / 2, height / 2);
		graphicsContext.drawLine((int) (width + size / Math.sqrt(2) + bandSize) / 2,
				height / 2, width, height / 2);
		graphicsContext.setColor(COLOR);
		graphicsContext.fill(new Polygon([ (width - size) / 2,
				(width - size) / 2,
				(int) ((width - size) / 2 + size / Math.sqrt(2)) ] as int[], [
				(height - size) / 2, (height + size) / 2, height / 2 ] as int[], 3));
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(bandSize));
		graphicsContext.drawLine((int) ((width - size) / 2 + size / Math.sqrt(2)),
				(height - size) / 2, (int) ((width - size) / 2 + size / Math.sqrt(2)), (height + size) / 2);
		int finSize = 2 * width / 32;
		graphicsContext.drawLine((int) ((width - size) / 2 + size / Math.sqrt(2)),
				(height - size) / 2, (int) ((width - size) / 2 + size  / Math.sqrt(2) + finSize), (height - size) / 2);
		graphicsContext.drawLine(
				(int) ((width - size) / 2 + size / Math.sqrt(2) + finSize),
				(height - size) / 2, (int) ((width - size) / 2 + size / Math.sqrt(2) + finSize), (height - size) / 2
						+ finSize);

		graphicsContext.drawLine((int) ((width - size) / 2 + size / Math.sqrt(2)),
				(height + size) / 2, (int) ((width - size) / 2 + size / Math.sqrt(2) - finSize), (height + size) / 2);
		graphicsContext.drawLine(
				(int) ((width - size) / 2 + size / Math.sqrt(2) - finSize),
				(height + size) / 2, (int) ((width - size) / 2 + size / Math.sqrt(2) - finSize), (height + size) / 2
						- finSize);
	}

	@Override
	protected void decorateComponentBody(GraphicsContext graphicsContext, boolean outlineMode) {
		double width = getWidth().convertToPixels();
		int finSize = (int) (width / 6);
		int bandSize = (int) BAND_SIZE.convertToPixels();
		graphicsContext.setColor(getBodyColor());
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(bandSize));
		graphicsContext.drawPolyline([
				(int) (width / Math.sqrt(2) + bandSize) + finSize + 1,
				(int) (width / Math.sqrt(2) + bandSize) + finSize + 1,
				(int) (width / Math.sqrt(2) + bandSize),
				(int) (width / Math.sqrt(2) + bandSize),
				(int) (width / Math.sqrt(2) + bandSize) - finSize - 1,
				(int) (width / Math.sqrt(2) + bandSize) - finSize - 1 ] as int[],
				[ (int) finSize, 0, 0, (int) width, (int) width,
						(int) (width - finSize) ] as int[], 6);
	}
}
