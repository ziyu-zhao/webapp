package com.example.csye6225_zzy.service;

import com.example.csye6225_zzy.mapper.UserMapper;
import com.example.csye6225_zzy.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void addUser(User user) {
        userMapper.addUser(user);
    }

    @Override
    public User selectByName(String name) {
        return userMapper.selectByName(name);
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }
}
