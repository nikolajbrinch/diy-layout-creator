package org.diylc.specifications.ic

import org.diylc.core.components.registry.SpecificationTypeRegistry;
import org.diylc.core.components.registry.SpecificationTypesLoader
import org.junit.Test

import java.nio.file.Path

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

public class TestSpecificationTypesLoader {

    @Test
    public void testCreate() {
        SpecificationTypeRegistry specificationTypeRegistry = new SpecificationTypesLoader().load(Thread.currentThread().contextClassLoader, new Path[0])

        assertNotNull(specificationTypeRegistry)

        assertNotNull(specificationTypeRegistry.lookup('IC'))

        assertEquals(ICTypePropertyModel.class, specificationTypeRegistry.lookup('IC'))
    }
}
