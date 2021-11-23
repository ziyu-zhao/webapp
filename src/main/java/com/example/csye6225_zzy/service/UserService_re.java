package com.example.csye6225_zzy.service;

import com.example.csye6225_zzy.mapper_re.UserMapper_re;
import com.example.csye6225_zzy.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService_re implements UserMapper_re {

    @Autowired
    private UserMapper_re userMapper_re;

    @Override
    public User selectByName(String name) {
        return userMapper_re.selectByName(name);
    }

}
