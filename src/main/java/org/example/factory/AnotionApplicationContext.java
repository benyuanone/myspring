package org.example.factory;

import org.example.annotion.Autowire;
import org.example.annotion.Component;
import org.example.annotion.Scanpath;
import org.example.annotion.Scope;
import org.example.config.BeanDefiniton;
import org.example.config.BeanInit;
import org.example.config.BeanPostProcessor;
import org.example.proxy.ProxyInstance;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AnotionApplicationContext {
    private Class configClass;

    private ConcurrentHashMap<String,Object> singleBean=new ConcurrentHashMap<>();
    private Map<String,Object> earlySingletonObjects=new HashMap<>();
    private ConcurrentHashMap<String, BeanDefiniton> beanDefMap=new ConcurrentHashMap<>();
    private List<BeanPostProcessor> processorList=new ArrayList<>();
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public AnotionApplicationContext(Class appConfig){

        this.configClass=appConfig;
        scan(appConfig);
        for (String key:beanDefMap.keySet()){
            BeanDefiniton beanDefiniton=beanDefMap.get(key);
            getBean(beanDefiniton.getBeanName());

        }
    }

    private void scan(Class appConfig) {
        Scanpath scanpath= (Scanpath) appConfig.getDeclaredAnnotation(Scanpath.class);
        String path=scanpath.value();
        ClassLoader classLoader= AnotionApplicationContext.class.getClassLoader();
        URL resource=classLoader.getResource(path.replace(".","/"));
        File fileParent= new File(resource.getFile());
        if(null!=fileParent && fileParent.isDirectory()){
            File[] files=fileParent.listFiles();
            for (File file:files){
                String fileName=file.getAbsolutePath();
                if(fileName.endsWith(".class")){
                    String className=fileName.substring(fileName.indexOf("org"),fileName.indexOf(".class")).replace("/",".");
                    try {
                        Class classn=classLoader.loadClass(className);
                        if(classn.isAnnotationPresent(Component.class)){
                            if(BeanPostProcessor.class.isAssignableFrom(classn)){
                                processorList.add((BeanPostProcessor)classn.getDeclaredConstructor().newInstance());
                                continue;
                            }
                            String beanName=((Component) classn.getDeclaredAnnotation(Component.class)).value();
                            BeanDefiniton beanDefiniton=new BeanDefiniton();
                            beanDefiniton.setBeanClass(classn);
                            beanDefiniton.setBeanName(beanName);
                            if(classn.isAnnotationPresent(Scope.class)) {
                                beanDefiniton.setScope(((Scope) classn.getDeclaredAnnotation(Scope.class)).value());
                            }else{
                                beanDefiniton.setScope("single");
                            }
                            beanDefMap.put(beanName,beanDefiniton);

                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public Object getBean(String beanName){
        if(beanDefMap.containsKey(beanName)){
            BeanDefiniton bdf= beanDefMap.get(beanName);
            if(bdf.getScope().equals("single")){
                Object bean=singleBean.get(beanName);
                if(null==bean && singletonsCurrentlyInCreation.contains(beanName)){
                    bean=earlySingletonObjects.get(beanName);
                }
                if(null!=bean){
                    return bean;
                }
                return createObject(bdf);
            }
             return createBeanRegin(bdf.getBeanClass());
        }else{
            throw new NullPointerException("不存在该bean");
        }
    }

    private Object createObject(BeanDefiniton bdf) {
        Object object= doCreateBean(bdf);
        singleBean.put(bdf.getBeanName(),object);
        singletonsCurrentlyInCreation.remove(bdf.getBeanName());
        earlySingletonObjects.remove(bdf.getBeanName());
        return object;
    }

    Object createBeanRegin(Class<?> beanClass){
        try {

            return new ProxyInstance().getProxy(beanClass);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Object doCreateBean(BeanDefiniton bdf) {
       Class clazz= bdf.getBeanClass();
        Object bean= null;
        try {
            bean = createBeanRegin(bdf.getBeanClass());
            earlySingletonObjects.put(bdf.getBeanName(),bean);
            singletonsCurrentlyInCreation.add(bdf.getBeanName());
            Field [] fiels= clazz.getDeclaredFields();
            for(Field field:fiels){
                if(field.isAnnotationPresent(Autowire.class)){
                    field.setAccessible(true);
                    field.set(bean,getBean(field.getName()));
                }
            }
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
