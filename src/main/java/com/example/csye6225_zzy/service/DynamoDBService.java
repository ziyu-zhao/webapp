package com.example.csye6225_zzy.service;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class DynamoDBService {

    @Autowired
    private DynamoDB dynamoDB;

    private String tableName = "csye6225-users";

    private Table table;

    private static final long EXPIRE = 2*60*1000;

    @PostConstruct
    public void init() throws InterruptedException {

        TableCollection<ListTablesResult> tables = dynamoDB.listTables();
        Iterator<Table> iterator = tables.iterator();

        while (iterator.hasNext()) {
            Table table1 = iterator.next();
            if(table1.getTableName().equals(tableName)){
                table = table1;
                break;
            }
        }

        if (table==null){
            try {
                List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
                attributeDefinitions.add(new AttributeDefinition().withAttributeName("ID").withAttributeType("S"));
                attributeDefinitions.add(new AttributeDefinition().withAttributeName("TTL").withAttributeType("N"));


                List<KeySchemaElement> keySchema = new ArrayList<>();
                keySchema.add(new KeySchemaElement().withAttributeName("ID").withKeyType(KeyType.HASH));
                keySchema.add(new KeySchemaElement().withAttributeName("TTL").withKeyType(KeyType.RANGE));

                CreateTableRequest request = new CreateTableRequest().withTableName(tableName)
                        .withAttributeDefinitions(attributeDefinitions)
                        .withKeySchema(keySchema)
                        .withProvisionedThroughput(new ProvisionedThroughput()
                                .withReadCapacityUnits(5L)
                                .withWriteCapacityUnits(5L));

                table = dynamoDB.createTable(request);

                table.waitForActive();
            } catch (Exception e){
                System.out.println("table can not be created");
                e.printStackTrace();
            }

        }

        TableDescription tableDescription = table.describe();
        System.out.format(
                "Name: %s:\n" + "Status: %s \n" + "Provisioned Throughput (read capacity units/sec): %d \n"
                        + "Provisioned Throughput (write capacity units/sec): %d \n",
                tableDescription.getTableName(), tableDescription.getTableStatus(),
                tableDescription.getProvisionedThroughput().getReadCapacityUnits(),
                tableDescription.getProvisionedThroughput().getWriteCapacityUnits());
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
        String token;
        try {
            item = table.getItem("ID", ID, "ID, token, TTL", null);

        }catch (Exception e){
            e.printStackTrace();
        }
        if (item!=null){
            Map<String,Object> map = item.asMap();
            if (Long.parseLong(String.valueOf(map.get("TTL")))<=System.currentTimeMillis()){
                token = (String) item.asMap().get("token");
                return token;
            }


            try {
                table.deleteItem("ID",ID);
            }catch (Exception e){
                e.printStackTrace();
            }

            return "expired";
        }

        return null;
    }
}
