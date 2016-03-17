package org.diylc.core.components.registry;

import java.io.IOException;
import java.nio.file.Path;

import org.diylc.core.ProgressView;

public class SpecificationRegistryFactory {

    private final SpecificationTypesLoader specificationTypesLoader = new SpecificationTypesLoader();

    private final SpecificationsLoader specificationsLoader = new SpecificationsLoader();

    public SpecificationRegistry newSpecificationRegistry(ProgressView progressView, Path[] specificationDirectories) throws IOException {
        SpecificationTypeRegistry specificationTypeRegistry = getSpecificationTypesLoader().loadSpecificationTypes(
                Thread.currentThread().getContextClassLoader(), specificationDirectories);
        SpecificationRegistry specificationRegistry = getSpecificationsLoader().loadSpecifications(specificationTypeRegistry,
                Thread.currentThread().getContextClassLoader(), specificationDirectories, progressView);
        
        return specificationRegistry;
    }
    
    private SpecificationsLoader getSpecificationsLoader() {
        return specificationsLoader;
    }

    private SpecificationTypesLoader getSpecificationTypesLoader() {
        return specificationTypesLoader;
    }

}
