package org.diylc.components.semiconductors;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

import org.diylc.core.CreationMethod;
import org.diylc.core.IDIYComponent;
import org.diylc.core.annotations.ComponentDescriptor;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;

@ComponentDescriptor(name = "Diode (schematic symbol)", author = "Branislav Stojkovic", category = "Semiconductors", creationMethod = CreationMethod.POINT_BY_POINT, instanceNamePrefix = "D", description = "Diode schematic symbol", zOrder = IDIYComponent.COMPONENT)
public class DiodeSymbol extends AbstractDiodeSymbol {

	private static final long serialVersionUID = 1L;

	public static Size BAND_SIZE = new Size(0.01, SizeUnit.in);

	public void drawIcon(Graphics2D g2d, int width, int height) {
		int size = width * 3 / 8;
		int bandSize = 2 * width / 32;
		g2d.rotate(-Math.PI / 4, width / 2, height / 2);
		g2d.setColor(LEAD_COLOR);
		g2d.drawLine(0, height / 2, (width - size) / 2, height / 2);
		g2d.drawLine((int) (width + size / Math.sqrt(2) + bandSize) / 2,
				height / 2, width, height / 2);
		g2d.setColor(COLOR);
		g2d.fill(new Polygon(new int[] { (width - size) / 2,
				(width - size) / 2,
				(int) ((width - size) / 2 + size / Math.sqrt(2)) }, new int[] {
				(height - size) / 2, (height + size) / 2, height / 2 }, 3));
		g2d.fill(new Rectangle2D.Double((int) ((width - size) / 2 + size
				/ Math.sqrt(2)), (height - size) / 2, bandSize, size));
	}

	@Override
	protected void decorateComponentBody(Graphics2D g2d, boolean outlineMode) {
		double width = getWidth().convertToPixels();
		int bandSize = (int) BAND_SIZE.convertToPixels();
		g2d.setColor(getBodyColor());
		g2d
				.fillRect((int) (width / Math.sqrt(2)) + 1, 0, bandSize,
						(int) width);
	}
}
