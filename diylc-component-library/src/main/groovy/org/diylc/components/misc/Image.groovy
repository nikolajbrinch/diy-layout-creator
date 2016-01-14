package org.diylc.components.misc

import org.diylc.components.Colors

import java.awt.AlphaComposite
import java.awt.Composite
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Shape
import java.awt.geom.Rectangle2D

import javax.swing.ImageIcon

import org.diylc.appframework.miscutils.IconImageConverter
import org.diylc.components.AbstractTransparentComponent
import org.diylc.components.ComponentDescriptor
import org.diylc.core.ComponentState
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty

import com.thoughtworks.xstream.annotations.XStreamConverter
import org.diylc.core.graphics.GraphicsContext

@ComponentDescriptor(name = "Image", author = "Branislav Stojkovic", category = "Misc", description = "User defined image", instanceNamePrefix = "Img", zOrder = IDIYComponent.COMPONENT, flexibleZOrder = true, stretchable = false, bomPolicy = BomPolicy.NEVER_SHOW)
class Image extends AbstractTransparentComponent<Void> {

    public static String DEFAULT_TEXT = "Double click to edit text"

    static final long serialVersionUID = 1L
	static ImageIcon ICON
	static byte DEFAULT_SCALE = 50

	Point point = new Point(0, 0)
	
    Void value = null
    
    @XStreamConverter(IconImageConverter.class)
    @EditableProperty(defaultable = false)
	ImageIcon image

    @EditableProperty(defaultable = false)
	byte scale = 50

	static {
		String name = "image.png"
		java.net.URL imgURL = Image.class.getResource("/images/" + name)

		if (imgURL != null) {
			ICON = new ImageIcon(imgURL, name)
		}
	}

	Image() {
		this.image = ICON
	}


	@Override
	void draw(GraphicsContext graphicsContext, ComponentState componentState,
					 boolean outlineMode, Project project,
					 IDrawingObserver drawingObserver) {
		double s = 1d * scale / DEFAULT_SCALE
		Shape clip = graphicsContext.getClip().getBounds()
		if (!clip.intersects(new Rectangle2D.Double(point.getX(), point.getY(),
				image.getIconWidth() * s, image.getIconHeight() * s))) {
			return
		}
		Composite oldComposite = graphicsContext.getComposite()
		if (alpha < Colors.MAX_ALPHA) {
			graphicsContext.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
		}

		graphicsContext.scale(s, s)
		graphicsContext.drawImage(image.getImage(), (int) (point.x / s),
				(int) (point.y / s), null)
		if (componentState == ComponentState.SELECTED) {
			graphicsContext.setComposite(oldComposite)
			graphicsContext.scale(1 / s, 1 / s)
			graphicsContext.setColor(Colors.SELECTION_COLOR)
			graphicsContext.drawRect(point.x, point.y, (int) (image.getIconWidth() * s),
					(int) (image.getIconHeight() * s))
		}
	}

	@Override
	void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		graphicsContext.drawImage(ICON.getImage(), point.x, point.y, null)
	}

	@Override
	int getControlPointCount() {
		return 1
	}

	@Override
	Point getControlPoint(int index) {
		return point
	}

	@Override
	boolean isControlPointSticky(int index) {
		return false
	}

	@Override
	VisibilityPolicy getControlPointVisibilityPolicy(int index) {
		return VisibilityPolicy.NEVER
	}

	@Override
	void setControlPoint(Point point, int index) {
		this.point.setLocation(point)
	}

	@Override
	String getName() {
		return super.getName()
	}

}
