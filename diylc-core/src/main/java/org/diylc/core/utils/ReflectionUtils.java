package org.diylc.core.utils;

import java.io.IOException;
import java.lang.reflect.Field;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ReflectionUtils {

    public static Class<?> findRequiredClass(String className) {
        Class<?> clazz;
        
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        
        return clazz;
    }
    
//    public static <T> T dynamicCast(Object item, Class<T> type) {
//        T castedItem = null;
//
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            castedItem = objectMapper.readValue(objectMapper.writeValueAsString(item), type);
//        } catch (IOException e) {
//            /*
//             * Ignore
//             */
//        }
//
//        return castedItem;
//    }

    public static boolean isInstanceOf(Object object, String classname) {
        Class<?> clazz = null;

        try {
            clazz = Class.forName(classname);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }

        return clazz != null ? clazz.isAssignableFrom(object.getClass()) : false;
    }

    public static Object getStaticProperty(Class<?> clazz, String propertyName) {
        Object value = null;
        
        try {
            Field field = clazz.getField(propertyName);
            value = field.get(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            /*
             * Ignore
             */
        }
        
        return value;
    }
}
