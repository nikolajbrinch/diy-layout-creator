package specifications

import org.diylc.specifications.ic.ICSpecification
import org.diylc.specifications.registry.SpecificationTypeRegistry;
import org.diylc.specifications.registry.SpecificationTypesLoader;
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

        assertEquals(ICSpecification.class, specificationTypeRegistry.lookup('IC'))
    }
}
