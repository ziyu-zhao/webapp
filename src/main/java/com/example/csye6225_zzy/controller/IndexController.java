package com.example.csye6225_zzy.controller;

import com.alibaba.fastjson.JSON;
import com.example.csye6225_zzy.mapper.UserMapper;
import com.example.csye6225_zzy.pojo.User;
import com.example.csye6225_zzy.pojo.UserIn;
import com.example.csye6225_zzy.utils.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Api(description = "API available without authentication",tags = "public")
@CrossOrigin
@RestController
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @ApiOperation("create new user")
    @PostMapping(path = "/v1/user", produces = "application/json")
    public String createUser(@RequestBody String json){
        UserIn userin = JSON.parseObject(json,UserIn.class);
        User user = new User(UUID.randomUUID().toString(),
                            userin.getFirst_name(),
                            userin.getLast_name(),
                            userin.getPassword(),
                            userin.getUsername(),
                            format.format(new Date()),
                            format.format(new Date()));


        userMapper.addUser(user);
        response.addHeader("token", JWTUtil.sign(userin.getUsername(),userin.getPassword()));


        return JSON.toJSONString(user);
    }


    @ApiOperation("welcome page")
    @GetMapping("/")
    public String hello(){
        return "hello";
    }
}
