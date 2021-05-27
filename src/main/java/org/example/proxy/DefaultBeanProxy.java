package org.example.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author yuanguangxin
 */
public class DefaultBeanProxy implements MethodInterceptor {

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Method beforeMethod = AspectUtils.getBeforeAdvisorMethod(method.getName());


        Object result = methodProxy.invokeSuper(object, args);



        return result;
    }
}
