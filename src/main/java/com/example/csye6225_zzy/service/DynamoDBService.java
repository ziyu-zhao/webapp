package com.example.csye6225_zzy.service;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DynamoDBService {

    @Autowired
    private DynamoDB dynamoDB;

    private String tableName = "csye6225-users";

    private Table table;

    private static final long EXPIRE = 2*60*1000;

    @PostConstruct
    public void init() throws InterruptedException {
        table = dynamoDB.getTable(tableName);
        System.out.println("table not null");
        if (table==null){
            try {
                List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
                attributeDefinitions.add(new AttributeDefinition().withAttributeName("ID").withAttributeType("S"));
                attributeDefinitions.add(new AttributeDefinition().withAttributeName("token").withAttributeType("S"));
                attributeDefinitions.add(new AttributeDefinition().withAttributeName("TTL").withAttributeType("N"));


                List<KeySchemaElement> keySchema = new ArrayList<>();
                keySchema.add(new KeySchemaElement().withAttributeName("ID").withKeyType(KeyType.HASH));

                CreateTableRequest request = new CreateTableRequest().withTableName(tableName)
                        .withAttributeDefinitions(attributeDefinitions)
                        .withKeySchema(keySchema);

                table = dynamoDB.createTable(request);

                table.waitForActive();
            } catch (Exception e){
                System.out.println("table can not be created");
                e.printStackTrace();
            }

        }
    }

    public void createItems(String ID, String token){
        try {
            Item item = new Item().withPrimaryKey("ID",ID)
                    .withString("token",token)
                    .withNumber("TTL",System.currentTimeMillis()+EXPIRE);
            table.putItem(item);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getItem(String ID){
        Item item = null;
        String token = null;
        try {
            item = table.getItem("ID", ID, "ID, token, TTL", null);

        }catch (Exception e){
            e.printStackTrace();
        }
        if (item!=null){
            Map<String,Object> map = item.asMap();
            if (Long.parseLong(String.valueOf(map.get("TTL")))<=System.currentTimeMillis())
               token = (String) item.asMap().get("token");

            try {
                table.deleteItem("ID",ID);
            }catch (Exception e){
                e.printStackTrace();
            }

            return token;
        }

        return null;
    }
}