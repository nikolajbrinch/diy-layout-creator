package org.diylc.core.utils;

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


}
