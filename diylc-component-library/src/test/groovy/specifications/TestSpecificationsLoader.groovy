package specifications

import org.diylc.specifications.registry.SpecificationRegistry;
import org.diylc.specifications.registry.SpecificationTypeRegistry;
import org.diylc.specifications.registry.SpecificationTypesLoader;
import org.diylc.specifications.registry.SpecificationsLoader;
import org.junit.Test

import java.nio.file.Path
import java.nio.file.Paths

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertEquals

public class TestSpecificationsLoader {

    @Test
    public void testCreateFromFileSystem() {
        SpecificationTypeRegistry specificationTypeRegistry = new SpecificationTypesLoader().load(Thread.currentThread().contextClassLoader, new Path[0])

        Path[] directories = [
            Paths.get(new File('./src/main/resources/specifications').absolutePath)] as Path[]

        SpecificationRegistry specificationRegistry = new SpecificationsLoader().load(
                specificationTypeRegistry, directories)

        assertNotNull(specificationRegistry)
        assertEquals(26, specificationRegistry.get('IC').size())
    }

    @Test
    public void testCreateFromClasspath() {
        ClassLoader classLoader = Thread.currentThread().contextClassLoader
        SpecificationTypeRegistry specificationTypeRegistry = new SpecificationTypesLoader().load(classLoader, new Path[0])

        SpecificationRegistry specificationRegistry = new SpecificationsLoader().load(
                specificationTypeRegistry, classLoader)

        assertNotNull(specificationRegistry)
        assertEquals(26, specificationRegistry.get('IC').size())
    }

    @Test
    public void testCreateFromFileSystemAndClasspath() {
        ClassLoader classLoader = Thread.currentThread().contextClassLoader
        SpecificationTypeRegistry specificationTypeRegistry = new SpecificationTypesLoader().load(classLoader, new Path[0])

        Path[] directories = [
            Paths.get(new File('./src/main/resources/specifications').absolutePath)] as Path[]

        SpecificationRegistry specificationRegistry = new SpecificationsLoader().load(
                specificationTypeRegistry, directories)

        assertNotNull(specificationRegistry)
        assertEquals(26, specificationRegistry.get('IC').size())
    }
}
