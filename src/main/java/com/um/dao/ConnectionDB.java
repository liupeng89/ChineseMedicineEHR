package com.um.dao;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ConnectionDB {
	
	/**
	 *  数据库连接
	 */
	
	public static MongoCollection<Document> getCollection(String db,String collection){
		
		MongoClient client = new MongoClient("localhost",27017);
		
		try {
			 MongoDatabase database = client.getDatabase(db);
		        
		     MongoCollection<Document> resultCollection = database.getCollection(collection);
		        
		     return resultCollection;
		} finally {
			// TODO: handle finally clause
			client.close();
		}
	}
		
//	/**
//	 *  Get the collection
//	 * @param collection
//	 * @return
//	 */
//	public static MongoCollection<Document> getCollections(String collection){
//		if(collection.equals("")){
//			return null;
//		}
//		
//		ApplicationContext context = new AnnotationConfigApplicationContext(DaoConnectionBean.class);
//		DaoConnectionBean daoConnectionBean = (DaoConnectionBean)context.getBean("daoConnectionBean");
//		// data operation
//		
//		MongoCollection<Document> coll = daoConnectionBean.getCollections(collection);
//		return coll;
//	}
	
}
