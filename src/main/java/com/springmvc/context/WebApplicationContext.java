package com.springmvc.context;

import com.springmvc.annotation.AutoWired;
import com.springmvc.annotation.Controller;
import com.springmvc.annotation.Service;
import com.springmvc.exceptions.ContextException;
import com.springmvc.xml.XmlPaser;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebApplicationContext {
    //classpath:springmvc.xml
    String contextConfigLocation;
    List<String> classNameList=new ArrayList<>();
    //Spring的IOC容器
    public Map<String ,Object> iocMap=new ConcurrentHashMap<>();

    public WebApplicationContext(String contextConfigLocation) {
        this.contextConfigLocation=contextConfigLocation;
    }

    /**
     * 初始化Spring容器
     * */
    public void refresh() {
        //1.解析Springmvc.xml  Dom4J解析
        String basePackage=XmlPaser.getBasePackage(contextConfigLocation.split(":")[1]);
        String[] basePackages=basePackage.split(",");
        if(basePackages.length>0){
            //com.project.controller,com.project.service
            for(String pack:basePackages){
                excuteScanPackage(pack);
            }
            System.out.println("scanned packages: "+classNameList);
        }
        //实例化Spring容器中的bean
        excuteInstance();
        //IOC容器中的对象是：
        System.out.println("ioc objects: "+iocMap);
        //实现spring容器中对象的注入
        excuteAutowire();
    }
    /**
     * 实现spring容器中的对象注入
     * */
    private void excuteAutowire() {
        if(iocMap.isEmpty()){
            throw  new ContextException("没有找到初始化的bean对象");
        }
        for(Map.Entry<String,Object> entry:iocMap.entrySet()){
           String key=entry.getKey();
           Object bean=entry.getValue();
           Field[] declaredFields=bean.getClass().getDeclaredFields();
           for(Field declaredField:declaredFields){
               // 找到被AutoWired注解的field
               if(declaredField.isAnnotationPresent(AutoWired.class)){
                   AutoWired autoWiredAnnotation=declaredField.getAnnotation(AutoWired.class);
                   String beanName=autoWiredAnnotation.value();
                   if("".equals(beanName)){
                       Class<?>type=declaredField.getType();
                       beanName=type.getSimpleName().substring(0,1).toLowerCase()+type.getSimpleName().substring(1);
                   }
                   declaredField.setAccessible(true);
                   //属性注入 调用反射给属性赋值
                   try {
                       declaredField.set(bean,iocMap.get(beanName));
                   } catch (IllegalAccessException e) {
                       e.printStackTrace();
                   }
               }
           }
        }
    }

    /**
     * 实例化容器中的bean对象
     * */
    private void excuteInstance() {
        if(classNameList.size()==0){
            throw new ContextException("There are not class to be instance！");
        }
        for(String className:classNameList){
            try {
                Class<?> clazz=Class.forName(className);
                if(clazz.isAnnotationPresent(Controller.class)){
                    //控制层的类 com.xx.Controller
                    //userController
                    String beanName=clazz.getSimpleName().substring(0,1).toLowerCase()+clazz.getSimpleName().substring(1);
                    iocMap.put(beanName,clazz.newInstance());
                }
                else if(clazz.isAnnotationPresent(Service.class)){
                    Service serviceAnnotation=clazz.getAnnotation(Service.class);
                    String beanName=serviceAnnotation.value();
                    if("".equals(beanName)){
                        Class<?>[] interfaces=clazz.getInterfaces();

                        for(Class<?> c1:interfaces){
                            String beanName1=c1.getSimpleName().substring(0,1).toLowerCase()+c1.getSimpleName().substring(1);
                            iocMap.put(beanName1,clazz.newInstance());
                        }
                    }
                    else{
                        iocMap.put(beanName,clazz.newInstance());
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 扫描包
     * */
    public void excuteScanPackage(String pack){
        // /com/project/service
        URL url=this.getClass().getClassLoader().getResource("/"+pack.replaceAll("\\.","/"));
        String path=url.getFile();
        File dir=new File(path);
        for(File f:dir.listFiles()){
            if(f.isDirectory()){
                //当前是一个文件目录 com.project.service.impl
                excuteScanPackage(pack+"."+f.getName());
            }else{
                //文件目录下的文件
                String className=pack+"."+f.getName().replaceAll(".class","");
                classNameList.add(className);
            }
        }
    }
}
