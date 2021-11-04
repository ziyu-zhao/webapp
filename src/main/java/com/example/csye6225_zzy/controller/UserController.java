package com.example.csye6225_zzy.controller;


import com.alibaba.fastjson.JSON;
import com.example.csye6225_zzy.mapper.FileMapper;
import com.example.csye6225_zzy.mapper.UserMapper;
import com.example.csye6225_zzy.pojo.AmazonFileModel;
import com.example.csye6225_zzy.pojo.User;
import com.example.csye6225_zzy.service.AmazonService;
import com.example.csye6225_zzy.utils.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private AmazonService amazonService;

    @Autowired
    private FileMapper fileMapper;

    private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @ApiOperation("get user information")
    @GetMapping("/v1/user/self")
    public String getUser(){
        String token = request.getHeader("token");
        if (token==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "unauthorized, get default user";
        }

        String username = JWTUtil.getName(token);
        User user = userMapper.selectByName(username);
        if (user==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not found, get default user";
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
            return "unauthorized, get default user";
        }

        String username = JWTUtil.getName(token);
        User user = userMapper.selectByName(username);
        if (user==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not found, get default user";
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

    @ApiOperation("upload/update user profile")
    @PostMapping(value = "/v1/user/self/pic")
    public String uploadProfile(@RequestParam("file")@Nullable MultipartFile file){
        if (file==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "no file uploaded";
        }
        String token = request.getHeader("token");

        if (token==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "unauthorized";
        }

        String username = JWTUtil.getName(token);
        User user = userMapper.selectByName(username);
        if (user==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not found";
        }

        String ID = user.getID();
        AmazonFileModel amazonFileModel= null;
        try{
            amazonFileModel= amazonService.upload(file,ID);
        }catch (Exception e){
            e.printStackTrace();
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "error, cannot upload to bucket";
        }

        amazonFileModel.setUploadTime(format.format(new Date()));
        if (fileMapper.searchByID(ID)!=null){
            fileMapper.updateFile(amazonFileModel);
        }else {
            fileMapper.addFile(amazonFileModel);
        }

        return JSON.toJSONString(amazonFileModel);
    }

    @ApiOperation("get user profile")
    @GetMapping("/v1/user/self/pic")
    public String getProfile(){
        String token = request.getHeader("token");

        if (token==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "unauthorized";
        }

        String username = JWTUtil.getName(token);
        User user = userMapper.selectByName(username);
        if (user==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not found";
        }

        AmazonFileModel amazonFileModel = fileMapper.searchByID(user.getID());
        if (amazonFileModel==null){
            return "user profile not found";
        }

        return JSON.toJSONString(amazonFileModel);

    }

    @ApiOperation("delete user profile")
    @DeleteMapping(value = "/v1/user/self/pic")
    public String deleteProfile(){
        String token = request.getHeader("token");

        if (token==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "unauthorized";
        }

        String username = JWTUtil.getName(token);
        User user = userMapper.selectByName(username);
        if (user==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not found";
        }

        try{
            amazonService.delete(user.getID());
        }catch (Exception e){
            e.printStackTrace();
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "error, cannot delete";
        }

        fileMapper.deleteFile(user.getID());
        return "deleted";
    }

}
