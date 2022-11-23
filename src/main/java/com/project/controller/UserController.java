package com.project.controller;


import com.project.bean.User;
import com.project.service.UserService;
import com.springmvc.annotation.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class UserController {
    //持有业务逻辑对象
    @AutoWired
    UserService userService;

    // restful风格请求测试
    @RequestMapping(value = "/test/{id}",method =RequestMapping.GET)
    @ResponseBody
    public Map<String,String> testGET(HttpServletRequest request, HttpServletResponse response,
                                      @PathVariable("id") String id){
        String res="GET id is "+id;
        Map<String,String> map=new HashMap<>();
        map.put("type",res);
        return map;
    }
    @RequestMapping(value = "/test/{id}",method =RequestMapping.POST)
    @ResponseBody
    public Map<String,String> testPost(HttpServletRequest request, HttpServletResponse response,
                                       @PathVariable("id") String id){
        String res="POST id is "+id;
        Map<String,String> map=new HashMap<>();
        map.put("type",res);
        return map;
    }
    @RequestMapping(value = "/test/{id}",method =RequestMapping.DELETE)
    @ResponseBody
    public Map<String,String> testDELETE(HttpServletRequest request, HttpServletResponse response,
                                         @PathVariable("id") String id){
        String res="POST id is "+id;
        Map<String,String> map=new HashMap<>();
        map.put("type",res);
        return map;
    }
    @RequestMapping(value = "/test/{id}",method =RequestMapping.PUT)
    @ResponseBody
    public Map<String,String> testPUT(HttpServletRequest request, HttpServletResponse response,
                                      @PathVariable("id") String id){
        String res="PUT id is "+id;
        Map<String,String> map=new HashMap<>();
        map.put("type",res);
        return map;
    }


    @RequestMapping(value = "/index")
    public String home(HttpServletRequest request, HttpServletResponse response){
        System.out.println("被调用了方法index");
        return "index.jsp";
    }

    @RequestMapping(value = "/upload",method = RequestMapping.POST)
    public String upload(HttpServletRequest request,HttpServletResponse response) throws Exception {
        //new FileUpload().upload(request,response);
        System.out.println("文件上传...");
        // 使用fileupload组件完成文件上传
        // 上传的位置
        String path = request.getSession().getServletContext().getRealPath("/uploads/");
        System.out.println(path);
        // 判断，该路径是否存在
        File file = new File(path);
        if(!file.exists()){
            // 创建该文件夹
            file.mkdirs();
        }
        // 解析request对象，获取上传文件项
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        // 解析request
        List<FileItem> items = upload.parseRequest(request);
        // 遍历
        for(FileItem item:items){
            // 进行判断，当前item对象是否是上传文件项
            if(item.isFormField()){
                // 说明普通表单向
            }else{
                // 说明上传文件项
                // 获取上传文件的名称
                String filename = item.getName();
                System.out.println ("get filename : "+filename);
                // 完成文件上传
                item.write(new File(path,filename));
                // 删除临时文件
                item.delete();
            }
        }
        return  "info.jsp";
    }
	

    //添加用户
    @RequestMapping(value = "/user/add")
    public String addUser(HttpServletRequest request, HttpServletResponse response,
                            @RequestParm("id") String id,@RequestParm("name") String name,@RequestParm("pass") String pass){
        System.out.println("被调用了方法add");
        response.setContentType("text/html;charset=utf-8");
        String addResult = userService.createUser(Integer.parseInt(id),name,pass);
        request.setAttribute("message",addResult);
        try {
            response.getWriter().print("addResult: "+addResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
		
        return "forward:/user.jsp";
    }

    @RequestMapping(value = "/user/query")
    public String findUsers(HttpServletRequest request, HttpServletResponse response,
                          @RequestParm("name") String name){
        System.out.println("被调用了方法find");
        response.setContentType("text/html;charset=utf-8");
        String userMessage=userService.getUserMessage(name);
        request.setAttribute("message",userMessage);
        try {
            response.getWriter().print("this is "+name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //转发到 user.jsp springmvc默认就是转发
        return "forward:/user.jsp";
    }

    // 查询全部用户信息
    @RequestMapping(value = "/user/findAll")
    public String findAll(HttpServletRequest request, HttpServletResponse response){
        System.out.println("被调用了方法find");
        response.setContentType("text/html;charset=utf-8");
        List<User> users=userService.findAllUsers();
        StringBuilder userMessage = new StringBuilder();
        for (User user : users) {
            userMessage.append(user.toString()).append("\n");
        }
        request.setAttribute("message", userMessage.toString());
        try {
            response.getWriter().print(userMessage.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //转发到 user.jsp springmvc默认就是转发
        return "forward:/user.jsp";
    }

    @RequestMapping(value = "/user/queryjson")
    @ResponseBody
    public List<User> findUsersQuery(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParm("name") String name){
        List<User> users = userService.findUsers(name);
        return users;
    }


    // 更新用户信息
    @RequestMapping(value = "/user/update")
    public String updateUser(HttpServletRequest request, HttpServletResponse response,
                          @RequestParm("id") String id,@RequestParm("name") String name,@RequestParm("pass") String pass){
        System.out.println("被调用了方法update");
        response.setContentType("text/html;charset=utf-8");
        String updateResult = userService.updateUser(Integer.parseInt(id),name,pass);
        request.setAttribute("message",updateResult);
        try {
            response.getWriter().print("updateResult: "+updateResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "forward:/user.jsp";
    }

    // 删除选中用户
    @RequestMapping(value = "/user/del")
    public String deleteUser(HttpServletRequest request, HttpServletResponse response,
                          @RequestParm("name") String name){
        response.setContentType("text/html;charset=utf-8");
        String deleteResult = userService.deleteUser(name);
        request.setAttribute("message",deleteResult);
        try {
            response.getWriter().print("deleteResult: "+deleteResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "forward:/user.jsp";
    }

    // 删除全部用户
    @RequestMapping(value = "/user/delAll")
    public String deleteAll(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("text/html;charset=utf-8");
        String deleteResult = userService.deleteAllUsers();
        request.setAttribute("message",deleteResult);
        try {
            response.getWriter().print("deleteResult: "+deleteResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "forward:/user.jsp";
    }
	
}
