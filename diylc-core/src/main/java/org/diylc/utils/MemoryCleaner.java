package org.diylc.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryCleaner {

	private static final Logger LOG = LoggerFactory.getLogger(MemoryCleaner.class);

	public static void clean(Object obj) {
		Class<?> clazz = obj.getClass();
		LOG.debug("Cleaning up object of type: " + clazz.getName());
		try {
			Method disposeMethod = clazz.getMethod("dispose");
			disposeMethod.invoke(obj);
		} catch (Exception e) {
			LOG.error("Error cleaning out object " + obj.hashCode() + " of type " + clazz.getName(), e);
		}
		// If it's a collection loop over and dispose elements.
		if (Collection.class.isAssignableFrom(clazz)) {
			LOG.info("Cleaning up collection elements");
			for (Object childObj : (Collection<?>) obj) {
				clean(childObj);
			}
		}
		for (Field field : clazz.getFields()) {
			if (!Modifier.isFinal(field.getModifiers())) {
				try {
					LOG.info("Cleaning up field: " + field.getName());
					Object childObj = field.get(obj);
					field.set(obj, null);
					clean(childObj);
				} catch (Exception e) {
					LOG.error("Error cleaning field " + field.getName() + " from type " + clazz.getName(), e);
				}
			}
		}
	}
}
