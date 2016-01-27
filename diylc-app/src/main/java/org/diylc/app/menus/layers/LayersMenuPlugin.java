package org.diylc.app.menus.layers;

import java.awt.event.ActionEvent;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.diylc.app.view.EventType;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.window.IPlugIn;
import org.diylc.app.window.ISwingUI;
import org.diylc.app.window.IView;
import org.diylc.core.IDIYComponent;

public class LayersMenuPlugin implements IPlugIn {

	private static final String LOCK_LAYERS_TITLE = "Layers";

	private IPlugInPort plugInPort;
	private Map<Layer, Action> lockActionMap;
	private Map<Double, Action> selectAllActionMap;

	public LayersMenuPlugin(ISwingUI swingUI) {
		lockActionMap = new HashMap<Layer, Action>();
		selectAllActionMap = new HashMap<Double, Action>();
		for (Layer layer : Layer.values()) {
			final double zOrder = layer.getZOrder();
			AbstractAction lockAction = new AbstractAction("Lock") {

				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					LayersMenuPlugin.this.plugInPort.setLayerLocked(zOrder,
							(Boolean) getValue(Action.SELECTED_KEY));
					selectAllActionMap.get(zOrder).setEnabled(
							!(Boolean) getValue(Action.SELECTED_KEY));
				}
			};
			lockAction.putValue(IView.CHECK_BOX_MENU_ITEM, true);
			lockActionMap.put(layer, lockAction);

			AbstractAction selectAllAction = new AbstractAction("Select All") {

				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					LayersMenuPlugin.this.plugInPort.selectAll(zOrder);
				}
			};
			selectAllActionMap.put(zOrder, selectAllAction);

			swingUI.injectSubmenu(layer.title, null, LOCK_LAYERS_TITLE);
			swingUI.injectMenuAction(lockAction, layer.title);
			swingUI.injectMenuAction(selectAllAction, layer.title);
		}
	}

	@Override
	public void connect(IPlugInPort plugInPort) {
		this.plugInPort = plugInPort;
	}

	@Override
	public EnumSet<EventType> getSubscribedEventTypes() {
		return EnumSet.of(EventType.LAYER_STATE_CHANGED);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processMessage(EventType eventType, Object... params) {
		if (eventType == EventType.LAYER_STATE_CHANGED) {
			Set<Integer> lockedLayers = (Set<Integer>) params[0];
			for (Layer layer : Layer.values()) {
				lockActionMap.get(layer).putValue(Action.SELECTED_KEY,
						lockedLayers.contains(layer.getZOrder()));
			}
		}
	}

	static enum Layer {
		CHASSIS("Chassis", IDIYComponent.CHASSIS), BOARD("Board",
				IDIYComponent.BOARD), TRACE("Trace", IDIYComponent.TRACE), COMPONENT(
				"Component", IDIYComponent.COMPONENT), TEXT("Text",
				IDIYComponent.TEXT);

		String title;
		double zOrder;

		private Layer(String title, double order) {
			this.title = title;
			zOrder = order;
		}

		public String getTitle() {
			return title;
		}

		public double getZOrder() {
			return zOrder;
		}
	}
}
