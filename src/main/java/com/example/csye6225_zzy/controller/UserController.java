package com.example.csye6225_zzy.controller;


import com.alibaba.fastjson.JSON;
import com.example.csye6225_zzy.pojo.AmazonFileModel;
import com.example.csye6225_zzy.pojo.User;
import com.example.csye6225_zzy.service.AmazonService;
import com.example.csye6225_zzy.service.DynamoDBService;
import com.example.csye6225_zzy.service.FileService;
import com.example.csye6225_zzy.service.UserService;
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
    private UserService userService;

    @Autowired
    private DynamoDBService dynamoDBService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AmazonService amazonService;

    @Autowired
    private FileService fileService;

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
        User user = userService.selectByName(username);
        if (user==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not found, get default user";
        }

        if (user.getVerified().equals("false")){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not verified";
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
        User user = userService.selectByName(username);
        if (user==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not found, get default user";
        }

        if (user.getVerified().equals("false")){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not verified";
        }

        if (!map.get("email").equals(user.getUsername())){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "username cannot be changed";
        }

        user.setLastname(map.get("lastname"));
        user.setFirstname(map.get("firstname"));
        user.setPassword(bCryptPasswordEncoder.encode(map.get("password")));
        user.setAccountUpdated(format.format(new Date()));

        userService.updateUser(user);

        Map<String,String> RUser = new HashMap<>();
        RUser.put("ID",user.getID());
        RUser.put("firstname",user.getFirstname());
        RUser.put("lastname",user.getLastname());
        RUser.put("username",user.getUsername());
        RUser.put("accountCreated",user.getAccountCreated());
        RUser.put("accountUpdated",user.getAccountUpdated());
        RUser.put("verifyTime",user.getVerifiedTime());

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
        User user = userService.selectByName(username);
        if (user==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not found";
        }

        if (user.getVerified().equals("false")){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not verified";
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
        if (fileService.searchByID(ID)!=null){
            fileService.updateFile(amazonFileModel);
        }else {
            fileService.addFile(amazonFileModel);
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
        User user = userService.selectByName(username);
        if (user==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not found";
        }

        if (user.getVerified().equals("false")){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not verified";
        }

        AmazonFileModel amazonFileModel = fileService.searchByID(user.getID());
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
        User user = userService.selectByName(username);
        if (user==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not found";
        }

        if (user.getVerified().equals("false")){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not verified";
        }

        try{
            amazonService.delete(user.getID());
        }catch (Exception e){
            e.printStackTrace();
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "error, cannot delete";
        }

        fileService.deleteFile(user.getID());
        return " deleted";
    }

    @GetMapping("/v1/user/verify/{username}/{verifyToken}")
    public String verifyUser(@PathVariable("username") String username,
                             @PathVariable("verifyToken") String verifyToken){
        User user = userService.selectByName(username);
        if (user==null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "user not found";
        }

        if (dynamoDBService.getItem(user.getID())==null){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "verifyToken expired or not found";
        }

        user.setVerified("true");
        user.setVerifiedTime(format.format(new Date()));

        userService.updateUser(user);

        Map<String,String> RUser = new HashMap<>();
        RUser.put("ID",user.getID());
        RUser.put("firstname",user.getFirstname());
        RUser.put("lastname",user.getLastname());
        RUser.put("username",user.getUsername());
        RUser.put("accountCreated",user.getAccountCreated());
        RUser.put("accountUpdated",user.getAccountUpdated());
        RUser.put("verifyTime",user.getVerifiedTime());

        return JSON.toJSONString(RUser);

    }

    @DeleteMapping(path = "/v1/user/{username}")
    public String deleteUser(@PathVariable("username") String username){
        User user = userService.selectByName(username);
        if (user==null) return "user not found";

        String ID = user.getID();
        userService.deleteUser(ID);
        if (fileService.searchByID(ID)!=null){
            try{
                amazonService.delete(ID);
                fileService.deleteFile(ID);
            }catch (Exception e){
                e.printStackTrace();
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return "error, cannot delete";
            }
        }
        return "deleted";
    }

}
