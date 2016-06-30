package org.diylc.core.components.registry;

import java.util.Collection;

import org.diylc.core.components.ComponentModel;
import org.diylc.specifications.Specification;

/**
 * Created by neko on 18/03/16.
 */
public interface ComponentRegistry {
    
    ComponentModels getComponentModels();

    ComponentModel getComponentModel(String modelId);

    Collection<Specification> getSpecifications(String category);
}
