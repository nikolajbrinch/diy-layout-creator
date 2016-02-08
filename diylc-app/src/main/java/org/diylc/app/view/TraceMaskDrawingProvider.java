package org.diylc.app.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.diylc.app.model.Model;
import org.diylc.app.view.canvas.IDrawingProvider;
import org.diylc.app.view.rendering.DrawingOption;
import org.diylc.components.PCBLayer;
import org.diylc.core.IDIYComponent;

/**
 * {@link IDrawingProvider} implementation that uses {@link IPlugInPort} to draw
 * a project onto the canvas.
 * 
 * @author Branislav Stojkovic
 */
public class TraceMaskDrawingProvider implements IDrawingProvider {

	private final View view;
    
	private final Model model;

	public TraceMaskDrawingProvider(View view, Model model) {
		super();
        this.view = view;
        this.model = model;
	}

	@Override
	public Dimension getSize() {
		return getView().getCanvasDimensions(false);
	}

	@Override
	public void draw(int page, Graphics g) {
		getView().draw((Graphics2D) g, EnumSet.of(DrawingOption.ANTIALIASING), new PCBLayerFilter(getUsedLayers()[page]));
	}

	@Override
	public int getPageCount() {
		return getUsedLayers().length;
	}

	private PCBLayer[] getUsedLayers() {
		Set<PCBLayer> layers = EnumSet.noneOf(PCBLayer.class);
		for (IDIYComponent c : getModel().getProject()
				.getComponents()) {
			Class<?> clazz = c.getClass();
			try {
				Method m = clazz.getMethod("getLayer");
				PCBLayer l = (PCBLayer) m.invoke(c);
				layers.add(l);
			} catch (Exception e) {
			}
		}
		List<PCBLayer> sorted = new ArrayList<PCBLayer>(layers);
		Collections.sort(sorted);
		return sorted.toArray(new PCBLayer[] {});
	}

    public Model getModel() {
        return model;
    }

    public View getView() {
        return view;
    }
}
