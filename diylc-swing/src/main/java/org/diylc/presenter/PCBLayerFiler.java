package org.diylc.presenter;

import java.lang.reflect.Method;

import org.diylc.components.IComponentFiler;
import org.diylc.components.PCBLayer;
import org.diylc.core.IDIYComponent;

public class PCBLayerFiler implements IComponentFiler {

	private PCBLayer layer;

	public PCBLayerFiler(PCBLayer layer) {
		super();
		this.layer = layer;
	}

	@Override
	public boolean testComponent(IDIYComponent<?> component) {
		Class<?> clazz = component.getClass();
		try {
			Method m = clazz.getMethod("getLayer");
			PCBLayer l = (PCBLayer) m.invoke(component);
			return layer == l;
		} catch (Exception e) {
			return false;
		}		
	}
}
