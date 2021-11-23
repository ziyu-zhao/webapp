package com.example.csye6225_zzy.mapper_re;

import com.example.csye6225_zzy.pojo.AmazonFileModel;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMapper_re {

    AmazonFileModel searchByID(String ID);
}
