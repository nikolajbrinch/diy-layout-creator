package org.diylc.app.view.properties;

import org.diylc.app.view.IPlugInPort;
import org.diylc.core.IPropertyValidator;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.ValidationException;

public class PropertyController {

    private IPlugInPort plugInPort;

    public PropertyController(IPlugInPort plugInPort) {
        this.plugInPort = plugInPort;
    }

    public void updateProperty(PropertyWrapper property, Object oldValue, Object newValue) {
        IPropertyValidator validator = property.getValidator();
        
        try {
            validator.validate(newValue);
            plugInPort.applyPropertyToSelection(property);
        } catch (ValidationException e) {
            /*
             * Ignore
             */
        }
    }

}
