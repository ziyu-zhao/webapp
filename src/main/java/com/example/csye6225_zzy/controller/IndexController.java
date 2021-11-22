package com.example.csye6225_zzy.controller;

import com.alibaba.fastjson.JSON;
import com.amazonaws.util.EC2MetadataUtils;
import com.example.csye6225_zzy.pojo.User;
import com.example.csye6225_zzy.service.FileService;
import com.example.csye6225_zzy.service.UserService;
import com.example.csye6225_zzy.utils.JWTUtil;
import com.timgroup.statsd.StatsDClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Api(description = "API available without authentication",tags = "public")
@CrossOrigin
@RestController
public class IndexController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @ApiOperation("create new user")
    @PostMapping(path = "/v1/user", consumes = "application/json")
    public String createUser(@RequestBody String json){

        try {
            Map<String,String> map = JSON.parseObject(json,HashMap.class);
            String username = map.get("email");
            String password = map.get("password");
            if (!username.contains("@")){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "invalid email";
            }

            if (userService.selectByName(username)!=null){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "user exists";
            }

            User user = new User(UUID.randomUUID().toString(),
                    map.get("firstname"),
                    map.get("lastname"),
                    bCryptPasswordEncoder.encode(password),
                    username,
                    format.format(new Date()),
                    format.format(new Date()),
                    "false",
                    null);

            userService.addUser(user);
            response.addHeader("token", JWTUtil.sign(username,password));

            Map<String,String> RUser = new HashMap<>();
            RUser.put("ID",user.getID());
            RUser.put("firstname",user.getFirstname());
            RUser.put("lastname",user.getLastname());
            RUser.put("username",user.getUsername());
            RUser.put("accountCreated",user.getAccountCreated());
            RUser.put("accountUpdated",user.getAccountUpdated());
            return JSON.toJSONString(RUser);
        }catch (Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "bad info to create a user";
        }

    }

    @ApiOperation("welcome page")
    @GetMapping("/hello")
    public String hello(){
        return "hello, zzy!! ";
    }
}
