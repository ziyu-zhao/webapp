package com.example.csye6225_zzy.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户")
public class UserIn {
    @ApiModelProperty("firstName")
    public String first_name;
    @ApiModelProperty("lastName")
    public String last_name;
    @ApiModelProperty("password")
    public String password;
    @ApiModelProperty("username")
    public String username;
}
