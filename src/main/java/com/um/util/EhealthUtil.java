package com.um.util;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.um.dao.ConnectionDB;
import com.um.data.DataBaseSetting;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.EhealthRecordConverter;

public class EhealthUtil {
	
	/**
	 * Query records based on the conditions
	 * @param conditions
	 * @return
	 */
	public static List<EHealthRecord> getEhealthRecordListByConditions(Document conditions){
		if (conditions == null) {
			return null;
		}
		// Get the data collections
		final List<EHealthRecord> eHealthRecords = new ArrayList<EHealthRecord>();
		MongoCollection<Document> collection = ConnectionDB.getCollections(DataBaseSetting.ehealthcollection);
		// Query the records based on the conditions
		FindIterable<Document> iterable = collection.find(conditions);
		if(iterable == null){
			return null;
		}
		iterable.forEach(new Block<Document>() {

			@Override
			public void apply(Document document) {
				// TODO Auto-generated method stub
				EHealthRecord eHealthRecord = EhealthRecordConverter.toEHealthRecord(document);
				eHealthRecords.add(eHealthRecord);
			}
		});
		return eHealthRecords;
	}
	
	/**
	 * 
	 * @param document
	 * @return
	 */
	public static EHealthRecord getOneEhealthRecordByConditions(Document conditions){
		if(conditions == null){
			return null;
		}
		EHealthRecord eHealthRecord = null;
		MongoCollection<Document> collection = ConnectionDB.getCollections(DataBaseSetting.ehealthcollection);
		FindIterable<Document> iterable = collection.find(conditions);
		
		Document document = iterable.first();
		if(document != null){
			eHealthRecord = EhealthRecordConverter.toEHealthRecord(document);
		}
		return eHealthRecord;
	}
	
	
	/**
	 *  实现list中字符串的两两组合（无顺序）
	 * @param list
	 * @return
	 */
	public static List<String> getCombination(List<String> list){
		if(list == null || list.size() == 0){
			return null;
		}
		List<String> resultList = new ArrayList<String>();
		int length = list.size(); // length
		
		for(int i = 0; i < length - 1; i++){
			for(int j = i + 1; j < length ; j++){
				resultList.add(list.get(i) + "|" + list.get(j));
			}
		}
		return resultList;
	}
	
	/** 
	 *  test main
	 * @param argvs
	 */
	public static void main(String[] argvs){
		List<String> list = new ArrayList<String>();
		list.add("A");
		list.add("B");
		list.add("C");
		list.add("D");
		list.add("E");
		list.add("F");
		
		List<String> resultList = getCombination(list);
		for(String string : resultList){
			System.out.println(string);
		}
		
	}
}
