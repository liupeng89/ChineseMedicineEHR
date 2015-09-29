package com.um.dao;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 *  Singleton database node connection bean
 * @author heermaster
 *
 */
public class DaoConnectionBean {
	
	private final String host = "localhost";
	private final int    port = 27017;
	private final String database = "db";
	
	/**
	 *  Get the collections.
	 * @param collection
	 * @return
	 */
	public MongoCollection<Document> getCollections(String collection){
		MongoClient client = new MongoClient(this.host,this.port);
		
		try{
			
			MongoDatabase db = client.getDatabase(this.database);
			MongoCollection<Document> collecs = db.getCollection(collection);
			return collecs;
			
		}finally{
			
		}
	}
}
