package com.example.csye6225_zzy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmazonFileModel {

    private String fileName;

    private String url;

    private String ID;

    private String uploadTime;
}
