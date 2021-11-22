package com.example.csye6225_zzy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User implements Serializable{

    @Id
    private String ID;

    private String firstname;
    private String lastname;
    private String password;
    private String username;
    private String accountCreated;
    private String accountUpdated;
    private String verified;
    private String verfiiedTime;

}
