package org.example.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yuanguangxin
 */
public class AspectUtils {

    private static Map<Class<?>, Object> advisorInstanceMap = new HashMap<>();

    private static Map<String, Method> advisorBeforeMap = new HashMap<>();

    private static Map<String, Method> advisorAfterMap = new HashMap<>();


    public static Method getBeforeAdvisorMethod(String methodName) {
        for (Map.Entry<String, Method> entry : advisorBeforeMap.entrySet()) {
            if (methodName.matches(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static Method getAfterAdvisorMethod(String methodName) {
        for (Map.Entry<String, Method> entry : advisorAfterMap.entrySet()) {
            if (methodName.matches(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static Object getAdvisorInstance(Class<?> clazz) {
        return advisorInstanceMap.get(clazz);
    }
}
