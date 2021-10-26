package com.example.csye6225_zzy.mapper;

import com.example.csye6225_zzy.pojo.AmazonFileModel;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMapper {
    void addFile(AmazonFileModel file);

    AmazonFileModel searchByID(String ID);

    void updateFile(AmazonFileModel file);

    void deleteFile(String ID);
}
