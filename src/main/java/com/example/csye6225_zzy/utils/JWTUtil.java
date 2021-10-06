package com.example.csye6225_zzy.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtil {

    private static final long EXPIRE = 15*60*1000;

    private static final String key = "burutiaowu";

    public static String sign(String username, String password){
        Date date = new Date(System.currentTimeMillis()+EXPIRE);
        Algorithm algorithm = Algorithm.HMAC256(key);

        Map<String,Object> head = new HashMap<>();
        head.put("typ","JWT");
        head.put("alg","HS256");

        return JWT.create().withHeader(head)
                .withClaim("username",username)
                //.withClaim("password",password)
                .withExpiresAt(date)
                .sign(algorithm);
    }

    public static String getName(String token1){
        String token = token1.replaceAll(" ","");
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);
        String username = jwt.getClaim("username").asString();
        return username;
    }
}
