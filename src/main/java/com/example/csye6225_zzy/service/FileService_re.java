package com.example.csye6225_zzy.service;

import com.example.csye6225_zzy.mapper_re.FileMapper_re;
import com.example.csye6225_zzy.pojo.AmazonFileModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService_re implements FileMapper_re {

    @Autowired
    private FileMapper_re fileMapper_re;

    @Override
    public AmazonFileModel searchByID(String ID) {
        return fileMapper_re.searchByID(ID);
    }

}
