package org.diylc.core.components.registry;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.diylc.core.IDIYComponent;
import org.diylc.core.graphics.GraphicsContext;

public class ComponentIconLoader {

    public Icon loadIcon(IDIYComponent component) {
        Icon icon = null;

        try {
            Image image = new BufferedImage(Constants.ICON_SIZE, Constants.ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
            GraphicsContext graphicsContext = new GraphicsContext((Graphics2D) image.getGraphics());
            graphicsContext.graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphicsContext.graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            component.drawIcon(graphicsContext, Constants.ICON_SIZE, Constants.ICON_SIZE);
            icon = new ImageIcon(image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return icon;
    }

}
