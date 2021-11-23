package com.example.csye6225_zzy.service;

import com.example.csye6225_zzy.mapper.FileMapper;
import com.example.csye6225_zzy.pojo.AmazonFileModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService implements FileMapper {

    @Autowired
    private FileMapper fileMapper;

    @Override
    public void addFile(AmazonFileModel file) {
        fileMapper.addFile(file);
    }

    @Override
    public void updateFile(AmazonFileModel file) {
        fileMapper.updateFile(file);
    }

    @Override
    public void deleteFile(String ID) {
        fileMapper.deleteFile(ID);
    }
}
