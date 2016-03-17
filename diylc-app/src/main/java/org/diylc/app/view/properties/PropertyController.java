package org.diylc.app.view.properties;

import org.diylc.app.view.IPlugInPort;
import org.diylc.core.components.properties.IPropertyValidator;
import org.diylc.core.components.properties.PropertyDescriptor;

import org.diylc.core.ValidationException;
import org.diylc.core.config.Configuration;

/**
 * @author nikolajbrinch@gmail.com
 */
public class PropertyController {

    private IPlugInPort plugInPort;

    public PropertyController(IPlugInPort plugInPort) {
        this.plugInPort = plugInPort;
    }

    public void updateProperty(PropertyDescriptor property, Object oldValue, Object newValue) {
        IPropertyValidator validator = property.getValidator();
        
        try {
            validator.validate(newValue);
            property.setValue(newValue);
            plugInPort.applyPropertyToSelection(property);
        } catch (ValidationException e) {
            property.setValue(oldValue);
            /*
             * Ignore
             */
        }
    }

    public void closePanel() {
        Configuration.INSTANCE.setProperty(Configuration.Key.PROPERTY_PANEL, false);
        plugInPort.refresh();
    }

}
