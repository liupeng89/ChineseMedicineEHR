package com.um.dao;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.um.data.DataBaseSetting;

/**
 *  Singleton database node connection bean
 * @author heermaster
 *
 */
public class DaoConnectionBean {
	
	
	/**
	 *  Get the collections.
	 * @param collection
	 * @return
	 */
	public MongoCollection<Document> getCollections(String collection){
		MongoClient client = new MongoClient(DataBaseSetting.host,DataBaseSetting.port);
		
		try{
			
			MongoDatabase db = client.getDatabase(DataBaseSetting.database);
			MongoCollection<Document> collecs = db.getCollection(collection);
			return collecs;
			
		}finally{
			
		}
	}
}
