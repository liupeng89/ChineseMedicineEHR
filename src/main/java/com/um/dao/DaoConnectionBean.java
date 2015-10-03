package com.um.dao;

import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.um.data.DataBaseSetting;
import com.um.model.EHealthRecord;

/**
 *  Singleton database node connection bean
 * @author heermaster
 *
 */
public class DaoConnectionBean {
	
	public MongoClient client;
	public MongoDatabase db;
	public MongoCollection<Document> ehealthRecordCollection;
	public MongoCollection<Document> patientInfoCollection;
	public List<EHealthRecord> eHealthRecords;
	
	public DaoConnectionBean(){
		this.client = new MongoClient(DataBaseSetting.host,DataBaseSetting.port);
		this.db = client.getDatabase(DataBaseSetting.database);
		this.ehealthRecordCollection = db.getCollection(DataBaseSetting.ehealthcollection);
		this.patientInfoCollection = db.getCollection(DataBaseSetting.infocollection);
	}
	
	/**
	 *  Get the collections.
	 * @param collection
	 * @return
	 */
	public MongoCollection<Document> getCollections(String collection){
		if(collection.equals("ehealthdata")){
			return this.ehealthRecordCollection;
		}else if(collection.equals("patientinfo")){
			return this.patientInfoCollection;
		}else{
			return null;
		}
	}
}
