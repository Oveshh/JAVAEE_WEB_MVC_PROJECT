package com.project.repository;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.project.bean.User;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 单例数据库类
 */
public class MongoRepo {
    private MongoClient client;
    private MongoDatabase db;
    private String database;

    private Bson filter = null;
    private Bson query = null;

    public MongoRepo(String domain, int port, String databaseName,
                     String userName, String pass){
        database = databaseName;
        MongoCredential credential = MongoCredential.createCredential(
                userName,database,pass.toCharArray()
        );
        ServerAddress address = new ServerAddress(domain,port);
        client = new MongoClient(address, Collections.singletonList(credential));
        db = client.getDatabase(databaseName);
        System.out.println("Current database:"+databaseName);
    }

    //CURD interfaces
    public String create(String coll, User user){
        MongoCollection<Document> collection = db.getCollection(coll);
        Document userInfo = new Document()
                .append("id",user.getId())
                .append("name",user.getName())
                .append("pass",user.getPassword());
        try{
            collection.insertOne(userInfo);
        }catch (Exception e){
            return "insert failed";
        }
        return "insert succeed";
    }
    public List<User> findAll(String coll) throws IOException {
        MongoCollection<Document> collection = db.getCollection(coll);
        FindIterable<Document> cur = collection.find();
        Iterator iter = cur.iterator();
        List<User> findRes = new ArrayList<>();
        while(iter.hasNext()){
            Document doc = (Document) iter.next();
            findRes.add(
                    new User((Integer) doc.get("id"),(String) doc.get("name"),(String) doc.get("pass"))
            );
        }
        return findRes;
    }
    public List<User> findByName(String coll, String name){
        MongoCollection<Document> collection = db.getCollection(coll);
        FindIterable<Document> cur = collection.find();
        Iterator iter = cur.iterator();
        List<User> findRes = new ArrayList<>();
        while(iter.hasNext()){
            Document doc = (Document) iter.next();
            System.out.println("iter: current name:["+doc.get("name")+"]");
            if(doc.get("name").equals(name)){
                findRes.add(
                        new User((Integer) doc.get("id"),(String) doc.get("name"),(String) doc.get("pass"))
                );
                break;
            }
        }
        return findRes;
    }
    public String update(String coll, User renew){
        MongoCollection<Document> collection = db.getCollection(coll);
        // 不是新创建一个项，而是set
        Document renewInfo = new Document()
                .append("id",renew.getId())
                .append("name",renew.getName())
                .append("pass",renew.getPassword());
        Document query = new Document().append("name",renew.getName());
        UpdateResult result = collection.replaceOne(query,renewInfo);
        if(result.wasAcknowledged()){
            return "update succeed";
        }
        return "update failed";
    }
    public String deleteByName(String coll, String name){
        MongoCollection<Document> collection = db.getCollection(coll);
        Document query = new Document().append("name",name);
        if(collection.deleteMany(query).wasAcknowledged()){
            return "delete user: "+name+" succeed";
        }
        return "delete user: "+name+" failed";
    }
    public String deleteAll(String coll){
        MongoCollection<Document> collection = db.getCollection(coll);
        Document query = new Document();
        // Delete All documents from collection Using blank BasicDBObject
        if(collection.deleteMany(query).wasAcknowledged()){
            return "delete all succeed";
        }
        return "delete all failed";
    }
}
