package org.diylc.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.diylc.core.PropertyWrapper;
import org.junit.Test;


public class ComponentProcessorTest {

	@Test
	public void testExtractProperties() {
		List<PropertyWrapper> properties = ComponentProcessor.getInstance().extractProperties(
				MockDIYComponent.class);
		assertNotNull(properties);
		assertEquals(5, properties.size());
	};
}
