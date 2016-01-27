package org.diylc.app.view.properties;

import java.util.EnumSet;
import java.util.List;

import javax.swing.SwingConstants;

import org.diylc.app.view.EventType;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.window.BadPositionException;
import org.diylc.app.window.IPlugIn;
import org.diylc.app.window.ISwingUI;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.config.Configuration;
import org.diylc.core.config.ConfigurationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyPlugin implements IPlugIn {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyPlugin.class);

    private final ISwingUI swingUI;

    private IPlugInPort plugInPort;

    private PropertyPanel propertyPanel;

    public PropertyPlugin(ISwingUI swingUI) {
        this.swingUI = swingUI;
    }

    @Override
    public void connect(IPlugInPort plugInPort) {
        this.plugInPort = plugInPort;
        this.propertyPanel = new PropertyPanel(new PropertyController(plugInPort));
        
        try {
            if (Configuration.INSTANCE.getPropertyPanel()) {
                swingUI.injectGUIComponent(getPropertyPanel(), SwingConstants.RIGHT);
            }
        } catch (BadPositionException e) {
            LOG.error("Could not install property plugin", e);
        }

        Configuration.INSTANCE.addListener(Configuration.Key.PROPERTY_PANEL, new ConfigurationListener() {

            @Override
            public void onValueChanged(Object oldValue, Object newValue) {
                try {
                    if ((boolean) newValue) {
                        swingUI.injectGUIComponent(getPropertyPanel(), SwingConstants.RIGHT);
                    } else {
                        swingUI.removeGUIComponent(SwingConstants.RIGHT);
                    }
                } catch (BadPositionException e) {
                    LOG.error("Could not toggle property plugin", e);
                }
            }
        });
    }

    private PropertyPanel getPropertyPanel() {
        return propertyPanel;
    }

    @Override
    public EnumSet<EventType> getSubscribedEventTypes() {
        return EnumSet.of(EventType.SELECTION_CHANGED);
    }

    @Override
    public void processMessage(EventType eventType, Object... params) {
        if (eventType == EventType.SELECTION_CHANGED) {
            List<PropertyWrapper> properties = plugInPort.getMutualSelectionProperties();
            getPropertyPanel().displayProperties(properties);
        }
    }

}
