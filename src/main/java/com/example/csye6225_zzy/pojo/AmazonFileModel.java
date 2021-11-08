package com.example.csye6225_zzy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "amazon_file_model")
public class AmazonFileModel {

    private String filename;

    private String url;

    @Id
    private String ID;

    private String uploadTime;
}
