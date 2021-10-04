package com.example.csye6225_zzy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable{
    private String ID;
    private String firstname;
    private String lastname;
    private String password;
    private String username;
    private String accountCreated;
    private String accountUpdated;
}
