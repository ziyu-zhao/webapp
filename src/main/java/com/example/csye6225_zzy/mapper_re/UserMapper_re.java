package com.example.csye6225_zzy.mapper_re;

import com.example.csye6225_zzy.pojo.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper_re {

    User selectByName(String name);
}
