package org.diylc.components.registry;

import java.io.IOException;
import java.nio.file.Path;

import org.diylc.core.ProgressView;
import org.diylc.specifications.registry.SpecificationRegistry;
import org.diylc.specifications.registry.SpecificationTypeRegistry;
import org.diylc.specifications.registry.SpecificationTypesLoader;
import org.diylc.specifications.registry.SpecificationsLoader;

public class ComponentRegistryFactory {

    private final SpecificationTypesLoader specificationTypesLoader = new SpecificationTypesLoader();

    private final SpecificationsLoader specificationsLoader = new SpecificationsLoader();

    private final ComponentTypeLoader componentTypeLoader = new ComponentTypeLoader();

    public ComponentRegistry newComponentRegistry(ProgressView progressView, Path[] componentDirectories, Path[] specificationDirectories)
            throws IOException {
        SpecificationTypeRegistry specificationTypeRegistry = getSpecificationTypesLoader().loadSpecificationTypes(
                Thread.currentThread().getContextClassLoader(), specificationDirectories);
        SpecificationRegistry specificationRegistry = getSpecificationsLoader().loadSpecifications(specificationTypeRegistry,
                Thread.currentThread().getContextClassLoader(), specificationDirectories, progressView);
        ComponentTypes componentTypes = getComponentTypeLoader().loadComponentTypes(componentDirectories, progressView);

        return new ComponentRegistry(componentTypes, specificationRegistry);
    }

    private ComponentTypeLoader getComponentTypeLoader() {
        return componentTypeLoader;
    }

    private SpecificationsLoader getSpecificationsLoader() {
        return specificationsLoader;
    }

    private SpecificationTypesLoader getSpecificationTypesLoader() {
        return specificationTypesLoader;
    }

}
