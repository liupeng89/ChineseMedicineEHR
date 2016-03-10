package com.um.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.um.data.DataBaseSetting;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.EhealthRecordConverter;

public class DataBaseBean {
	
	private List<EHealthRecord> eHealthRecords;
	
	private List<String> batchList;
	
	public List<EHealthRecord> geteHealthRecords() {
		return eHealthRecords;
	}

	public void seteHealthRecords(List<EHealthRecord> eHealthRecords) {
		this.eHealthRecords = eHealthRecords;
	}

	public List<String> getBatchList() {
		return batchList;
	}

	public void setBatchList(List<String> batchList) {
		this.batchList = batchList;
	}

	// init the list
	public DataBaseBean() {
		
		eHealthRecords = new ArrayList<EHealthRecord>();
		batchList = new ArrayList<String>();
		
		MongoClient client = new MongoClient(DataBaseSetting.host,DataBaseSetting.port);
		MongoDatabase db = client.getDatabase(DataBaseSetting.database);
		MongoCollection<Document> ehealthRecordCollection = db.getCollection(DataBaseSetting.ehealthcollection);
		
		// List of ehealth record
		FindIterable<Document> iterable = ehealthRecordCollection.find();
		
		iterable.forEach(new Block<Document>() {

			@Override
			public void apply(Document document) {
				// TODO Auto-generated method stub
				EHealthRecord eHealthRecord = EhealthRecordConverter.toEHealthRecord(document);
	        	
	        	if(eHealthRecord != null){
	        		eHealthRecords.add(eHealthRecord);
	        	}
			}
		});
		
		// List of batch
		Set<String> batchSet = new HashSet<String>();
		
		for(EHealthRecord e : eHealthRecords){
			batchSet.add(e.getBatchString());
		}
		batchList = new ArrayList<String>();
		for(String s : batchSet){
			if(s.contains(".")){
				batchList.add(s.substring(0, 4).trim());
			}else{
				batchList.add(s);
			}
		}
		Collections.sort(batchList);
		Collections.reverse(batchList);
		
		System.out.println("all data size: " + eHealthRecords.size());
		System.out.println("batch" + batchList.toString());
		
		// close database
		client.close();
	}
	
	
	
}
