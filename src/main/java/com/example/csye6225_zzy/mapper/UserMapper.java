package com.example.csye6225_zzy.mapper;

import com.example.csye6225_zzy.pojo.User;

import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

    void addUser(User user);

    void updateUser(User user);

    void deleteUser(String ID);
}
