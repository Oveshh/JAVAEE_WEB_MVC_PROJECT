package com.project.service.impl;


import com.project.bean.User;
import com.project.repository.MongoRepo;
import com.project.service.UserService;
import com.springmvc.annotation.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private MongoRepo repo;

    public UserServiceImpl(){
        repo = new MongoRepo(
                "127.0.0.1",27017,"db1","root","root"
        );
    }

    @Override
    public String createUser(int id, String name, String pass) {
        return repo.create("user",new User(id,name,pass));
    }

    @Override
    public List<User> findUsers(String name) {
        return repo.findByName("user",name);
    }

    @Override
    public List<User> findAllUsers() {
        List<User> res;
        try{
            res = repo.findAll("user");
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return res;
    }

    @Override
    public String updateUser(int id, String name, String pass) {
        return repo.update("user",new User(id,name,pass));
    }

    @Override
    public String deleteUser(String name) {
        return repo.deleteByName("user",name);
    }

    @Override
    public String deleteAllUsers() {
        return repo.deleteAll("user");
    }

    @Override
    public String getUserMessage(String name) {
        List<User> findRes = repo.findByName("user",name);
        if(findRes.size()==0){
            return "没有找到对应的用户信息！";
        }
        return findRes.get(0).toString();
    }
}
