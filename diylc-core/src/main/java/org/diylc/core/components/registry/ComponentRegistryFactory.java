package org.diylc.core.components.registry;

import java.io.IOException;
import java.nio.file.Path;

import org.diylc.core.ProgressView;
import org.diylc.core.components.properties.PropertyDescriptorExtractor;

public class ComponentRegistryFactory {


    public ComponentRegistry newComponentRegistry(ProgressView progressView, Path[] componentDirectories, SpecificationRegistry specificationRegistry)
            throws IOException {
        ComponentModelLoader componentModelLoader = new ComponentModelLoader(
                new ComponentModelFactory(new PropertyDescriptorExtractor(specificationRegistry)));
        
        ComponentModels componentModels = componentModelLoader.loadComponentTypes(componentDirectories, progressView);

        return new ComponentRegistry(componentModels, specificationRegistry);
    }

}
