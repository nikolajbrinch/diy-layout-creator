package org.diylc.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.diylc.components.registry.PropertyExtractor;
import org.diylc.core.PropertyWrapper;
import org.junit.Test;

public class PropertyExtractorTests {

    @Test
    public void testExtractFieldProperties() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
            SecurityException, NoSuchMethodException {
        List<PropertyWrapper> properties = new PropertyExtractor().extractProperties(FieldComponent.class);

        assertNotNull(properties);
        assertEquals(2, properties.size());
        assertEquals("B", properties.get(0).getName());
        assertEquals("A", properties.get(1).getName());
        assertEquals(int.class, properties.get(0).getType());
        assertEquals(String.class, properties.get(1).getType());

        FieldComponent component = new FieldComponent();

        properties.get(0).setValue(11);
        properties.get(0).writeTo(component);

        assertEquals(11, component.getB());
        component.setB(12);
        properties.get(0).readFrom(component);
        assertEquals(12, properties.get(0).getValue());

        properties.get(1).setValue("neko");
        properties.get(1).writeTo(component);

        assertEquals("neko", component.getA());
        component.setA("oken");
        properties.get(1).readFrom(component);
        assertEquals("oken", properties.get(1).getValue());
    }

    @Test
    public void testExtractMethodProperties() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
            SecurityException, NoSuchMethodException {
        List<PropertyWrapper> properties = new PropertyExtractor().extractProperties(MethodComponent.class);

        assertNotNull(properties);
        assertEquals(2, properties.size());
        assertEquals("B", properties.get(0).getName());
        assertEquals("A", properties.get(1).getName());
        assertEquals(int.class, properties.get(0).getType());
        assertEquals(String.class, properties.get(1).getType());

        MethodComponent component = new MethodComponent();

        properties.get(0).setValue(11);
        properties.get(0).writeTo(component);

        assertEquals(11, component.getB());
        component.setB(12);
        properties.get(0).readFrom(component);
        assertEquals(12, properties.get(0).getValue());

        properties.get(1).setValue("neko");
        properties.get(1).writeTo(component);

        assertEquals("neko", component.getA());
        component.setA("oken");
        properties.get(1).readFrom(component);
        assertEquals("oken", properties.get(1).getValue());

    }

    @Test
    public void testExtracMixedProperties() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
            SecurityException, NoSuchMethodException {
        List<PropertyWrapper> properties = new PropertyExtractor().extractProperties(MixedComponent.class);

        assertNotNull(properties);
        assertEquals(2, properties.size());
        assertEquals("C", properties.get(0).getName());
        assertEquals("D", properties.get(1).getName());
        assertEquals(String.class, properties.get(0).getType());
        assertEquals(int.class, properties.get(1).getType());

        MixedComponent component = new MixedComponent();

        properties.get(0).setValue("neko");
        properties.get(0).writeTo(component);

        assertEquals("neko", component.getC());
        component.setC("oken");
        properties.get(0).readFrom(component);
        assertEquals("oken", properties.get(0).getValue());

        properties.get(1).setValue(11);
        properties.get(1).writeTo(component);

        assertEquals(11, component.getD());
        component.setD(12);
        properties.get(1).readFrom(component);
        assertEquals(12, properties.get(1).getValue());

    }
}
