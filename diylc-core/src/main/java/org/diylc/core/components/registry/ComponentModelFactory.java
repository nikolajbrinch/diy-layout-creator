package org.diylc.core.components.registry;

import org.diylc.core.IDIYComponent;
import org.diylc.core.components.ComponentModel;

/**
 * Created by neko on 18/03/16.
 */
public interface ComponentModelFactory {
    
    ComponentModel newComponentModel(String componentId, IDIYComponent componentInstance);
    
}
