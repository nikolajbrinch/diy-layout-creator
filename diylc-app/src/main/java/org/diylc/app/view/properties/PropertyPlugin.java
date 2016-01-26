package org.diylc.app.view.properties;

import java.util.EnumSet;

import javax.swing.SwingConstants;

import org.diylc.app.BadPositionException;
import org.diylc.app.EventType;
import org.diylc.app.IPlugIn;
import org.diylc.app.IPlugInPort;
import org.diylc.app.ISwingUI;
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
        try {
            swingUI.injectGUIComponent(getPropertyPanel(), SwingConstants.RIGHT);
        } catch (BadPositionException e) {
            LOG.error("Could not install property plugin", e);
        }
    }

    private PropertyPanel getPropertyPanel() {
        if (propertyPanel == null) {
            propertyPanel = new PropertyPanel();
        }
        
        return propertyPanel;
    }

    @Override
    public EnumSet<EventType> getSubscribedEventTypes() {
        return null;
    }

    @Override
    public void processMessage(EventType eventType, Object... params) {
    }

}
