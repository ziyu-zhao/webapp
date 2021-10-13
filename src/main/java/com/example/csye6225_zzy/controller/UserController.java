package com.example.csye6225_zzy.controller;


import com.alibaba.fastjson.JSON;
import com.example.csye6225_zzy.mapper.UserMapper;
import com.example.csye6225_zzy.pojo.User;
import com.example.csye6225_zzy.utils.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Api(description = "API requires authentication",tags = "authenticated")
@CrossOrigin
@RestController
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @ApiOperation("get user information")
    @GetMapping("/v1/user/self")
    public String getUser(){
        String token = request.getHeader("token");
        if (token==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "unauthorized, get default user:\n"+JSON.toJSONString(getDefaultUser());
        }

        String username = JWTUtil.getName(token);
        User user = userMapper.selectByName(username);
        if (user==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not found, get default user:\n"+JSON.toJSONString(getDefaultUser());
        }

        Map<String,String> RUser = new HashMap<>();
        RUser.put("ID",user.getID());
        RUser.put("firstname",user.getFirstname());
        RUser.put("lastname",user.getLastname());
        RUser.put("username",user.getUsername());
        RUser.put("accountCreated",user.getAccountCreated());
        RUser.put("accountUpdated",user.getAccountUpdated());
        return JSON.toJSONString(RUser);
    }

    @ApiOperation("update user information")
    @PutMapping(path = "/v1/user/self", consumes = "application/json")
    public String updateUser(@RequestBody Map<String, String> map){
        String token = request.getHeader("token");

        if (token==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "unauthorized, get default user:\n"+JSON.toJSONString(getDefaultUser());
        }

        String username = JWTUtil.getName(token);
        User user = userMapper.selectByName(username);
        if (user==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not found, get default user:\n"+JSON.toJSONString(getDefaultUser());
        }

        if (!map.get("email").equals(user.getUsername())){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "username cannot be changed";
        }

        user.setLastname(map.get("lastname"));
        user.setFirstname(map.get("firstname"));
        user.setPassword(bCryptPasswordEncoder.encode(map.get("password")));
        user.setAccountUpdated(format.format(new Date()));

        userMapper.updateUser(user);

        Map<String,String> RUser = new HashMap<>();
        RUser.put("ID",user.getID());
        RUser.put("firstname",user.getFirstname());
        RUser.put("lastname",user.getLastname());
        RUser.put("username",user.getUsername());
        RUser.put("accountCreated",user.getAccountCreated());
        RUser.put("accountUpdated",user.getAccountUpdated());
        return JSON.toJSONString(RUser);
    }

    private Map<String,String> getDefaultUser(){
        Map<String,String> dUser = new HashMap<>();
        dUser.put("ID",UUID.randomUUID().toString());
        dUser.put("firstname","ziyu");
        dUser.put("lastname","zhao");
        dUser.put("username","zhao.ziyu2@northeastern.edu");
        dUser.put("accountCreated",format.format(new Date()));
        dUser.put("accountUpdated",format.format(new Date()));
        return dUser;
    }
}
