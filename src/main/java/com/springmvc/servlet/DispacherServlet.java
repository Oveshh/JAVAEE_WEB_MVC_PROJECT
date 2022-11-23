package com.springmvc.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springmvc.annotation.*;
import com.springmvc.context.WebApplicationContext;
import com.springmvc.exceptions.ContextException;
import com.springmvc.handler.MyHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class DispacherServlet extends HttpServlet {
    private WebApplicationContext webApplicationContext;
    //URL和对象的方法映射关系
    List<MyHandler> handlerList=new ArrayList<>();
    @Override
    public void init() throws ServletException {
        //1.Servlet初始化的时候，读取初始化的参数 contextConfigLocation
        String contextConfigLocation =this.getServletConfig().getInitParameter("contextConfigLocation");
        //2.创建Spring容器
        webApplicationContext=new WebApplicationContext(contextConfigLocation);
        //3.初始化Spring容器
        webApplicationContext.refresh();
        //4.初始化请求映射
        initHandleMapping();
    }

    /**
     * 初始化请求映射
     */
    private void initHandleMapping() {
        //IOC map中没有对象，则抛出错误
        if(webApplicationContext.iocMap.isEmpty()){
            throw new ContextException("String 容器为空");
        }
        //否则对map中对象执行装入
        for(Map.Entry<String,Object> entry:webApplicationContext.iocMap.entrySet()){
            Class<?> clazz=entry.getValue().getClass();
            if(clazz.isAnnotationPresent(Controller.class)){
                Method[] declaredMethods=clazz.getDeclaredMethods();
                for(Method declaredMethod:declaredMethods){
                    if(declaredMethod.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping requestMappingAnnotation=declaredMethod.getAnnotation(RequestMapping.class);
                        String url=requestMappingAnnotation.value();
                        MyHandler handler=new MyHandler(url,entry.getValue(),declaredMethod);
                        handlerList.add(handler);
                    }
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //请求分发处理GET
        excuteDispacth(req,resp,"GET");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //请求分发处理POST
        excuteDispacth(req,resp,"POST");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //请求分发处理DELETE
        excuteDispacth(req,resp,"DELETE");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //请求分发处理PUT
        excuteDispacth(req,resp,"PUT");
    }

    //请求分发处理
    private void excuteDispacth(HttpServletRequest req, HttpServletResponse resp,String method) {
        System.out.println("enter method is "+method);
        MyHandler handler=getHandler(req,method);
        //精确匹配
        if(handler!=null){
            try {
                Class<?>[] parameterTypes=handler.getMethod().getParameterTypes();
                //定义一个参数的数组
                Object[] params=new Object[parameterTypes.length];
                for(int i=0;i< parameterTypes.length;i++){
                    Class<?> parameterType=parameterTypes[i];
                    if("HttpServletRequest".equals(parameterType.getSimpleName())){
                        params[i]=req;
                    }
                    else  if("HttpServletResponse".equals(parameterType.getSimpleName())){
                        params[i]=resp;
                    }
                }
                //获取请求中参数集合
                Map<String, String[]> parameterMap = req.getParameterMap();
                for(Map.Entry<String,String[]> entry:parameterMap.entrySet()){
                    String name=entry.getKey();
                    String value=entry.getValue()[0];

                    int index=hasRequestParm(handler.getMethod(),name);
                    System.out.println("index == -1? "+(index==-1));
                    if(index!=-1){
                        params[index]=value;
                    }
                    else{
                        List<String> names=getParameterNames(handler.getMethod());
                        System.out.println(names);
                        for(int i=0;i<names.size();i++){
                            if(name.equals(names.get(i))){
                                params[i]=value;
                                break;
                            }
                        }
                    }
                }
                System.out.println("get params:"+ Arrays.toString(params));

                //调用控制器方法并打印结果
                Object result = handler.getMethod().invoke(handler.getController(), params);
                System.out.println("handle result:"+result);

                //根据结果处理响应
                if(result instanceof String){
                    //跳转jsp
                    String viewName=(String)result;
                    if(viewName.contains(":")){
                        String viewType = viewName.split(":")[0];
                        String viewPage = viewName.split(":")[1];
                        //forward:/user.jsp
                        if(viewType.equals("forward")){
                            //转发
                            req.getRequestDispatcher(viewPage).forward(req,resp);
                        }else{
                            //redirect:/user.jsp
                            //重定向
                            resp.sendRedirect(viewPage);
                        }
                    }
                    else{
                        //默认就转发
                        req.getRequestDispatcher(viewName).forward(req,resp);
                    }
                }
                else{
                    //返回json数据
                    Method myMethod=handler.getMethod();
                    if(myMethod.isAnnotationPresent(ResponseBody.class)){
                        //把返回值调用json工具转化为json字符串
                        ObjectMapper objectMapper=new ObjectMapper();
                        String json=objectMapper.writeValueAsString(result);
                        resp.setContentType("text/html;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        out.print(json);
                        out.close();
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | ServletException | IOException e) {
                e.printStackTrace();
            }
        }
        else{
            //模糊匹配
            Map<String,Object> map=getSimilarHandlerAndValue(req,method);
            if(map==null)return;

            handler= (MyHandler) map.get("myHandler");
            Map<String,String> paramsMap= (Map<String, String>) map.get("paramsMap");
            try {
                Class<?>[] parameterTypes=handler.getMethod().getParameterTypes();
                //定义一个参数的数组
                Object[] params=new Object[parameterTypes.length];
                for(int i=0;i< parameterTypes.length;i++){
                    Class<?> parameterType=parameterTypes[i];
                    if("HttpServletRequest".equals(parameterType.getSimpleName())){
                        params[i]=req;
                    }
                    else  if("HttpServletResponse".equals(parameterType.getSimpleName())){
                        params[i]=resp;
                    }
                }
                //模糊链接中的Pathvalue值辅助给函数参数
                for(String key:paramsMap.keySet()){
                    int index=hasPathValue(handler.getMethod(),key);
                    System.out.println(key+"---"+index);
                    if(index!=-1){
                        params[index]=paramsMap.get(key);
                        System.out.println("has late:"+params[index]);
                    }
                }
                Map<String, String[]> parameterMap = req.getParameterMap();
                for(Map.Entry<String,String[]> entry:parameterMap.entrySet()){
                    String name=entry.getKey();
                    String value=entry.getValue()[0];
                    int index=hasRequestParm(handler.getMethod(),name);
                    System.out.println(index==-1);
                    if(index!=-1){
                        params[index]=value;
                    }
                    else{
                        List<String> names=getParameterNames(handler.getMethod());
                        System.out.println(names);
                        for(int i=0;i<names.size();i++){
                            if(name.equals(names.get(i))){
                                params[i]=value;
                                break;
                            }
                        }

                    }
                }
                //调用控制器方法
                Object result = handler.getMethod().invoke(handler.getController(), params);
                if(result instanceof String){
                    //跳转jsp
                    String viewName=(String)result;
                    if(viewName.contains(":")){
                        String viewType = viewName.split(":")[0];
                        String viewPage = viewName.split(":")[1];
                        //forward:/user.jsp
                        if(viewType.equals("forward")){
                            //转发
                            req.getRequestDispatcher(viewPage).forward(req,resp);
                        }else{
                            //redirect:/user.jsp
                            //重定向
                            resp.sendRedirect(viewPage);
                        }
                    }
                    else{
                        //默认就转发
                        req.getRequestDispatcher(viewName).forward(req,resp);
                    }
                }
                else{
                    //返回json数据
                    Method myMethod=handler.getMethod();
                    if(myMethod.isAnnotationPresent(ResponseBody.class)){
                        //把返回值调用json工具转化为json字符串
                        ObjectMapper objectMapper=new ObjectMapper();
                        String json=objectMapper.writeValueAsString(result);
                        resp.setContentType("text/html;charset=utf-8");
                        PrintWriter out = null;
                        try {
                            out = resp.getWriter();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        out.print(json);
                        out.close();
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | JsonProcessingException e) {
                e.printStackTrace();
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取请求对应的handler
     * */
    public MyHandler getHandler(HttpServletRequest req,String method){
        String RequestURI=req.getRequestURI();
        for(MyHandler myHandler:handlerList){
            if(myHandler.getUrl().equals(RequestURI)){
                System.out.println(RequestURI+" handler found: "+myHandler);
                if(myHandler.getMethod().getAnnotation(RequestMapping.class).method().equals(method)) {
                    return myHandler;
                }

            }
        }
        return null;
    }

    /**
     * 获取请求对应的map对应的handler和模糊参数map
     * */
    public Map<String, Object> getSimilarHandlerAndValue(HttpServletRequest req,String method){
        // 获取输入参数
        String[] requestURI = req.getRequestURI().split("/");
        Map<String,Object> map = new HashMap<>();
        // 循环查找
        for(MyHandler myHandler:handlerList){
            String handerUrl=myHandler.getUrl();
            String[] handerUrls=handerUrl.split("/");
            boolean flag=true;
            // 不可能匹配的情况
            if(handerUrls.length!=requestURI.length)continue;
            if(!handerUrl.contains("{")&&!handerUrl.contains("}"))continue;
            Map<String,String> paramsMap=new HashMap<>();
            for(int i=0;i< handerUrls.length;i++){
                String curr=handerUrls[i];
                if(curr.contains("{")&&curr.contains("}")){
                    String key=curr.substring(1,curr.length()-1);
                    System.out.println(key+"-----"+requestURI[i]);
                    paramsMap.put(key,requestURI[i]);
                    continue;
                }
                else if(!curr.equals(requestURI[i])){
                    flag=false;
                    break;
                }
            }
            if(flag){
                if(myHandler.getMethod().getAnnotation(RequestMapping.class).method().equals(method)) {
                    map.put("myHandler",myHandler);
                    map.put("paramsMap",paramsMap);
                    return map;
                }
            }
        }
        return null;
    }

    /**
     * 判断控制器方法参数是否有RequestParam注解，并且找到对应的value值，如果找到，返回这个参数的位置，没有找到则返回-1
     */
    public int hasRequestParm(Method method,String name){
        Parameter[] parameters = method.getParameters();
        for(int i=0;i< parameters.length;i++){
            Parameter p = parameters[i];
            boolean b=p.isAnnotationPresent(RequestParm.class);
            if(b){
                RequestParm requestParm = p.getAnnotation(RequestParm.class);
                String RequestParmvalue = requestParm.value();
                if(name.equals(RequestParmvalue)){
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 判断控制器方法参数是否有PathValue注解，并且找到对应的value值，如果找到，返回这个参数的位置，没有找到则返回-1
     */
    public int hasPathValue(Method method,String name){
        Parameter[] parameters = method.getParameters();
        for(int i=0;i< parameters.length;i++){
            Parameter p = parameters[i];
            boolean b=p.isAnnotationPresent(PathVariable.class);
            if(b){
                PathVariable pathVariable = p.getAnnotation(PathVariable.class);
                String RequestParmvalue = pathVariable.value();
                if(name.equals(RequestParmvalue)){
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 获取控制器方法参数
     */
    public List<String> getParameterNames(Method method){
        Parameter[] parameters = method.getParameters();
        List<String> list=new ArrayList<>();
        for(Parameter p:parameters){
            list.add(p.getName());
        }
        return list;
    }
}
