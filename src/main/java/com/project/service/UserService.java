package com.project.service;




import com.project.bean.User;

import java.util.List;

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
