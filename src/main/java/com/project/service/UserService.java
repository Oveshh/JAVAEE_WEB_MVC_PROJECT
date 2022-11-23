package com.project.service;




import com.project.bean.User;

import java.util.List;

// user 接口。 也是后端的操作接口。连通数据库。DAO层
public interface UserService {
    // CRUD
    String createUser(int id, String name, String pwd);
    List<User> findUsers(String name);
    List<User> findAllUsers();
    String updateUser(int id, String name, String pwd);
    String deleteUser(String name);
    String deleteAllUsers();
    String getUserMessage(String name);
}
