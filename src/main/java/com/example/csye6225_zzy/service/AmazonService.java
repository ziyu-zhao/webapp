package com.example.csye6225_zzy.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.example.csye6225_zzy.pojo.AmazonFileModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AmazonService {

    @Value("${custom.aws.bucket}")
    private String bucket;

    private String SNSTopicArn = "arn:aws:sns:us-east-1:713361818012:mytopic";

    private AmazonS3 amazonS3;

    private AmazonSNS amazonSNS;

    Map<String, MessageAttributeValue> smsAttributes;

    @PostConstruct
    public void init(){
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setProtocol(Protocol.HTTP);
        configuration.disableSocketProxy();

        AWSCredentialsProvider awsCredentialsProvider = new InstanceProfileCredentialsProvider(false);
        
        amazonS3 = AmazonS3ClientBuilder.standard()
                .withClientConfiguration(configuration)
                .withCredentials(awsCredentialsProvider)
                .withRegion(Regions.US_EAST_1)
                .enablePathStyleAccess()
                .build();

        amazonSNS = AmazonSNSClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(Regions.US_EAST_1)
                .build();

    }

    public AmazonFileModel upload(MultipartFile file, String uid){

        String originalFileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        long fileSize = file.getSize();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        objectMetadata.setContentLength(fileSize);

        try {
             amazonS3.putObject(bucket, uid, file.getInputStream(), objectMetadata);

        } catch (AmazonServiceException e) {
            System.out.println(e.getErrorMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        AmazonFileModel amazonFileModel = new AmazonFileModel ();
        amazonFileModel.setFilename(originalFileName);
        amazonFileModel.setUrl(bucket+"/"+uid+"/"+originalFileName);
        amazonFileModel.setID(uid);
        return amazonFileModel ;
    }

    public void delete(String uid){
        try {
            amazonS3.deleteObject(bucket, uid);
        } catch (AmazonServiceException e) {
            System.out.println(e.getErrorMessage());
        }
    }

    public void publishSNSMessage(String message){
        amazonSNS.publish(new PublishRequest()
                .withTopicArn(SNSTopicArn)
                .withMessage(message)
                .withMessageAttributes(getSmsAttributes()));
    }

    private Map<String, MessageAttributeValue> getSmsAttributes(){
        if (smsAttributes==null){
            Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
            smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                    .withStringValue("zzyweb") //The sender ID shown on the device.
                    .withDataType("String"));
            smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                    .withStringValue("0.50") //Sets the max price to 0.50 USD.
                    .withDataType("Number"));
            smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                    .withStringValue("Transactional") //Sets the type to promotional.
                    .withDataType("String"));
        }
        return smsAttributes;
    }
}
