package com.um.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;

import com.um.data.DiagClassifyData;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.MedicineStatics;

public class DiagMedicineProcess {
	
	/**
	 *  根据诊断关键字，对病例进行分类，
	 * @param diagnoses
	 * @return
	 */
	public static List<EHealthRecord> getRecordsByDiagnose(String[] diagnoses,List<EHealthRecord> eRecords){
		if(diagnoses == null || diagnoses.length == 0){
			return null;
		}
		List<EHealthRecord> classifiedRecords = new ArrayList<EHealthRecord>(); //分类之后的病例list
		Set<EHealthRecord> classifiedSet = new HashSet<EHealthRecord>();
		//扫描病例list，判断病例的中医诊断类型是否全部符合诊断关键字----最长匹配
		for(EHealthRecord e : eRecords){
			String diagnose = e.getChinesediagnostics();//中医诊断
			if(isMaxMatch(diagnose, diagnoses)){
				classifiedSet.add(e);
			}
		}
		classifiedRecords.addAll(classifiedSet);
		return classifiedRecords;
	}
	
	
	/**
	 *  根据中药，得出症状关键字
	 * @param medicine
	 * @return
	 */
	public static List<String> getDescriptionByMedicine(String medicine,List<EHealthRecord> eHealthRecords){
		if(medicine == "" || eHealthRecords == null || eHealthRecords.isEmpty()){
			return null;
		}
		//1. 判断没有病例对应的关键字
		Map<EHealthRecord,ArrayList<String>> recordMap = new HashMap<EHealthRecord, ArrayList<String>>();
		ArrayList<String> keywordList = null;
		String[] keywords = DiagClassifyData.cndescriclassify;
		// 2.根据中药判断所有的病例
		for(EHealthRecord e : eHealthRecords){
			String description = e.getConditionsdescribed();
			keywordList = new ArrayList<String>();
			for(String s : keywords){
				String[] keys = s.split("\\|");
				if(description.matches(".*" + keys[0].trim() + ".*" )){
					keywordList.add(keys[0].trim());
				}
			}
			if(!keywordList.isEmpty()){
				recordMap.put(e, keywordList);
			}
		}
		// 3. 取这些关键字的并集
		Set<EHealthRecord> ehealthSet = recordMap.keySet();
		Set<String> medicineSet = new HashSet<String>();
		for(EHealthRecord e : ehealthSet){
			if(e.getChineseMedicines() != null && !e.getChineseMedicines().isEmpty()){
				for(ChineseMedicine c : e.getChineseMedicines()){
					if(c.getNameString().equals(medicine.trim())){
						medicineSet.addAll(recordMap.get(e));
					}
				}
			}
		}
		List<String> resultList = new ArrayList<String>();
		resultList.addAll(medicineSet);
		return resultList;
	}
	
	
	/**
	 * 构建关键字参照表
	 * @return
	 */
	public static Map<String, HashMap<String,ArrayList<String>>> createReference(String[] keywords){
		// 1.构造参照表
		Map<String, HashMap<String,ArrayList<String>>> keyMap = new HashMap<String, HashMap<String,ArrayList<String>>>();
		for(String key : keywords){
			String[] projects = key.split("%");// 0:部位 1:描述
			String[] descriptions = projects[1].split("#"); //不同描述
			HashMap<String, ArrayList<String>> descMap = new HashMap<String, ArrayList<String>>();
			for(String s : descriptions){
				String[] desc = s.split(":");
				String[] descKey = desc[1].split("\\|");
				ArrayList<String> descList = (ArrayList<String>) DiagMedicineProcess.arrayToList(descKey);
				descMap.put(desc[0], descList);
			}
			keyMap.put(projects[0], descMap);
		}
		return keyMap;
	}
	
	
	/**
	 * 根据描述，得出中药处方
	 * @param description
	 * @param eHealthRecords
	 * @return
	 */
	public static Set<String> getMedicinesByDescription(String description,List<EHealthRecord> eHealthRecords){
		if( description.equals("") || eHealthRecords == null || eHealthRecords.size() == 0){
			return null;
		}
		// 1.Find records based on the description
		List<EHealthRecord> eRecords = getEhealthRecordByDescription(description,eHealthRecords); 
		if( eRecords == null || eRecords.size() == 0 ){
			return null;
		}
		
		// 2. Statistics the Chinese medicines
		Map<String, Integer> medicineMap = statisEhealthMedicine(eRecords);
		medicineMap = DiagMedicineProcess.sortMapByValue(medicineMap); //sorted
		
		Set<String> medicineSet = medicineMap.keySet();
		return medicineSet;
	}
	
	
	/**
	 *  Get the similiar ehrs based on the description
	 *  
	 * @param description
	 * @param eHealthRecords
	 * @return
	 */
	public static List<EHealthRecord> getEhealthRecordByDescription(String description,List<EHealthRecord> eHealthRecords){
		if( description.equals("") || eHealthRecords == null || eHealthRecords.size() == 0){
			return null;
		}
		List<EHealthRecord> similarRecords = new ArrayList<EHealthRecord>(); // 同种描述的病历
		// 1. 处理description
		String[] descriptionSplits = description.split(",");
		if( descriptionSplits == null || descriptionSplits.length == 0 ) return null;
		
		// 2. 生成关键字编码表  <部位， < 状态：［k1,k2,k3.....］>>
		Map<String, String> normalTableMap = MedicineByDescription.convertArraysToMap(DiagClassifyData.normalAndBaddescription); // normal table
		Map<String, ArrayList<String>> descTableMap = MedicineByDescription.convertArraysToMapList(DiagClassifyData.descKeywords); // descrition keyword list
		
		// 去掉正常的status
		Set<String> descriptionSet = new HashSet<String>(); // 输入描述中的关键字
		for( String string : descriptionSplits ){
			if (normalTableMap.get(string) != null && !normalTableMap.get(string).equals("0")) {
				descriptionSet.add(string);
			}
		}
		if (descriptionSet == null || descriptionSet.size() == 0) return null;
		
		// 3. 对输入的病症描述进行编码
		Map<String, ArrayList<String>> inputCodeMap = new HashMap<String, ArrayList<String>>();
		
		for (String d : descriptionSet) {
			if (normalTableMap.get(d) == null || descTableMap.get(d) == null || normalTableMap.get(d).equals("0")) {
				continue;
			}
			inputCodeMap.put(d, descTableMap.get(d));
		}
		
		Set<String> keySet = inputCodeMap.keySet();
		// remove the time status no used
		if (keySet.contains("cmtreat")) { inputCodeMap.remove("cmtreat"); }
		if (keySet.contains("shuqian")) { inputCodeMap.remove("shuqian"); }
		if (keySet.contains("shuhou")) { inputCodeMap.remove("shuhou"); }
		if (keySet.contains("zhiliaozhong")) { inputCodeMap.remove("zhiliaozhong"); }
		if (keySet.contains("zhiliaohou")) { inputCodeMap.remove("zhiliaohou"); }
		if (keySet.contains("hualiaozhong")) { inputCodeMap.remove("hualiaozhong"); }
		if (keySet.contains("hualiaohou")) { inputCodeMap.remove("hualiaohou"); }
		if (keySet.contains("fenzi")) { inputCodeMap.remove("fenzi"); }
		if (keySet.contains("mianyi")) { inputCodeMap.remove("mianyi"); }
		
		Map<String, ArrayList<String>> mainInputCodeMap = new HashMap<String, ArrayList<String>>(); // Main description
		Map<String, ArrayList<String>> secondInputCodeMap = new HashMap<String, ArrayList<String>>(); // Second description
		
		// main description：all items should be matched
		for (String s : DiagClassifyData.mainDescriptionStrings) {
			if (keySet.contains(s)) {
				mainInputCodeMap.put(s, inputCodeMap.get(s));
			}
		}
		// second description: 50% items should be matched
		for (String s : DiagClassifyData.seconddescriptionStrings) {
			if (keySet.contains(s)) {
				secondInputCodeMap.put(s, inputCodeMap.get(s));
			}
		}
		// find the records 
		for (EHealthRecord eHealthRecord : eHealthRecords) {
			
			boolean isMainMatched = false;
			boolean isSecondMatched = false;
			
			// main description match
			if (mainInputCodeMap.size() > 0) {
				// main description need 100% match
				int size = mainInputCodeMap.size();
				isMainMatched = checkMatchBasedOnDescription(eHealthRecord, mainInputCodeMap, size);
			}
			// second description match
			if (secondInputCodeMap.size() > 0) {
				// Second description need 50% match
				int size = secondInputCodeMap.size() / 2 + 1;
				isSecondMatched = checkMatchBasedOnDescription(eHealthRecord, secondInputCodeMap, size);
			}
			// based on the main description without second description
			if (secondInputCodeMap.size() == 0) {
				isSecondMatched = true;
			}
			
			if (isMainMatched && isSecondMatched) {
				similarRecords.add(eHealthRecord);
			}
		}
		return similarRecords;
	}
	
	/**
	 * Check the record match the description or not.
	 * @param e
	 * @param description <status, contents > 
	 * @param size
	 * @return
	 */
	public static boolean checkMatchBasedOnDescription(EHealthRecord e, Map<String, ArrayList<String>> description, int size){
		
		int statusmatchnum = 0;
		Set<String> statusSet = description.keySet();
		
		for (String status : statusSet) {
			int contentmatchnum = 0; // 
			ArrayList<String> desckeywordlist = description.get(status);
			for (String c : desckeywordlist) {
				if (e.getConditionsdescribed().contains(c)) {
					statusmatchnum++;
					break;
				}
				contentmatchnum++;
			}
			if (contentmatchnum == desckeywordlist.size()-1) {
				// 该状态的关键字全部都不一致，则该病例不属于相似病例
				break;
			}
		}
		// The match conditions: the number of match bigger than half of status
		if (statusmatchnum >= size) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 *  检查描述是否和某一项的关键字数组是否匹配
	 * @param description
	 * @param keywords
	 * @return
	 */
	public static boolean checkDescriptionMatch(String description,String[] keywords){
		if( description == "" || keywords == null || keywords.length == 0 ){
			return false;
		}
		for( String k : keywords ){
			if(description.matches(".*" + k + ".*")){
				return true;
			}
		}
		return false;
	}
	
	/**
	 *  初始化描述对比表
	 *  	<部位， <状态， [k1,k2,k3.....]>>
	 * @return
	 */
	public static Map<String, HashMap<String, ArrayList<String>>> getDescriptionMap(String[] descriptionStrings){
//		String[] descriptionStrings = DiagClassifyData.cndescriptionkeywords1;
		Map<String, HashMap<String, ArrayList<String>>> descriptionMap = new HashMap<String, HashMap<String,ArrayList<String>>>();
		HashMap<String, ArrayList<String>> descHashMap = null;
		for(String s : descriptionStrings){
			descHashMap = new HashMap<String, ArrayList<String>>();
			String[] tmpStrings = s.split("%");  // 0:项目  1:描述
			String[] descStrings = tmpStrings[1].split("#");
			for(String ss : descStrings){
				if(ss.split(":").length == 2){
					String[] dStrings = ss.split(":");
					String[] listStrings = dStrings[1].split("\\|");
					ArrayList<String> list = new ArrayList<String>();
					for(String l : listStrings){
						list.add(l);
					}
					descHashMap.put(dStrings[0], list);
				}
			}
			descriptionMap.put(tmpStrings[0], descHashMap);
		}
		return descriptionMap;
	}
	
	/**
	 * 	统计出现概率大约 percent的中药
	 * 
	 * @param medicines----- 中药处方统计结果<名称，数量>
	 * @param length-------- 全部病历数量
	 * @param percent------- 百分比
	 * @return
	 */
	public static List<String> statisMedicineWithPercent(Map<String, Integer> medicines,int length,double percent){
		if(medicines == null || medicines.isEmpty()){
			return null;
		}
		List<String> medicineList = new ArrayList<String>(); // 中药名称
		
		Set<String> keys = medicines.keySet();
		
		for( String s : keys ){
			int count = (Integer)medicines.get(s);
			if((count * 1.0 / length) >= percent){
				// 大于percent
				medicineList.add(s);
			}
		}
		return medicineList;
	}
	
	
	/**
	 *  统计中药出现的概率
	 * 		主要功能：
	 * 				1、输入单味中药时，只算出现数量和概率
	 * 				2、输入多味中药时，计算出现概率的交集和并集
	 * @param medicines
	 * @return
	 */
	public static Map<String, String> statisMedicProbability(String medicines,List<EHealthRecord> allRecrods){
		if(medicines == ""){
			return null;
		}
		Map<String, String> resultMap = new HashMap<String, String>();
		// 1. 拆分中药名称
		
		String[] names = medicines.split(" ");
		if(names == null || names.length == 0){
			return null;
		}
		
		// 3. 计算数值
		if(names.length > 1){
			// 3.1 多味中药，需要计算交集和并集
			
			// 1、需要得出所有中药组合
			List<String> combinnationNames = null;
			if(names.length > 2){
				combinnationNames = MedicineStatics.combiantion(names); //中药组合
			}else{
				combinnationNames = new ArrayList<String>();
				String s = names[0].trim() + "|" + names[1].trim();
				combinnationNames.add(s);
			}
			
			for(String combiname : combinnationNames ){
				// 每一个组合 计算起交集和并集的情况
				String[] nStrings = combiname.split("\\|");
				if(nStrings.length == 0){
					continue;
				}
				// 2、计算这些组合的交集和并集
				List<String> medicinesList = new ArrayList<String>();
				for(String s : nStrings){
					medicinesList.add(s.trim());
				}
				//3.1.1 并集运算
				int union = EhealthRecordMath.getUnion(medicinesList, allRecrods); // 并集
				//3.1.2 交集运算
				int mix = EhealthRecordMath.getMix(nStrings, allRecrods);

				double unionPercent = 1.0 * union / allRecrods.size();
				double mixPercent   = 1.0 * mix   / allRecrods.size();
				resultMap.put(combiname, union+"|"+unionPercent+"%"+mix +"|" +mixPercent);
			}
		}else{
			// 单味中药，只要计算出现概率就可以
			List<EHealthRecord> results = null;
			
			if(allRecrods == null || allRecrods.size() == 0){
				return null;
			}
			
			results = new ArrayList<EHealthRecord>();
			
			// 判断是否同时出现在病历中
			for(EHealthRecord e : allRecrods){
				if(hasThisMedicine(e, names)){
					//同时出现在同一病历中
					results.add(e);
				}
			}
			// 3. 整理统计结果
			if(results == null || results.size() == 0){
				return null;
			}
			
			int count = results.size();
			double percent = 1.0 * count / allRecrods.size();
//			System.out.println("[all record]: " + allRecrods.size());
			resultMap.put(names[0], count + "|" + percent);
			
		}
		return resultMap;
	}
	
	
	/**
	 *  判断是否同时出现所有的中药
	 * @param e
	 * @param names
	 * @return
	 */
	public static boolean hasThisMedicine(EHealthRecord e,String[] names){
		if(e == null || e.getChineseMedicines() == null || 
				e.getChineseMedicines().size() == 0 || names == null || names.length == 0){
			return false;
		}
		
		List<ChineseMedicine> allMedicines = e.getChineseMedicines(); // 中药处方
		boolean hasMedicine = true;
		boolean flag = false;
		int length = allMedicines.size();
		for(String s : names){
			flag = false;
			for(int i = 0; i < length; i++){
				if(s.trim().equals(allMedicines.get(i).getNameString())){
					flag = true;// 同时出现则为true = 只要有一个不出现就为false 
				}
				if(!flag && i == length - 1){
					flag = false;
				}
			}
			hasMedicine = hasMedicine && flag;
		}
		
		return hasMedicine;
	}
	
	/**
	 *  统计病历list中的中药处方的数量
	 * @param eHealthRecords
	 * @return Map<中药名称，数量>
	 */
	public static Map<String, Integer> statisEhealthMedicine(List<EHealthRecord> eHealthRecords){
		if(eHealthRecords == null || eHealthRecords.size() == 0){
			return null;
		}
		//1、统计list中所有的中药名称
		List<String> allCnMedicines = new ArrayList<String>(); // 所有的中药名称（包含重复的项）
		for(EHealthRecord eRecord : eHealthRecords){
			if(eRecord.getChineseMedicines() != null && eRecord.getChineseMedicines().size() > 0){
				for(ChineseMedicine c : eRecord.getChineseMedicines()){
					//check the error data
					if (c.getNameString().contains("内服")||c.getNameString().contains("煎药机")) {
						continue;
					}
					allCnMedicines.add(c.getNameString());
				}
			}
		}
		
		//2、依次统计重复的名称
		Map<String, Integer> statisMedicines = MedicineStatics.staticsChineseMedicine(allCnMedicines);
		//4、返回结果
		return statisMedicines;
	}
	
	/**
     *  按值对map进行排序
     * @param orimap
     * @return
     */
    public static Map<String, Integer> sortMapByValue(Map<String, Integer> orimap){
    	if(orimap == null || orimap.isEmpty()){
    		return null;
    	}
    	
    	Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
    	
    	List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String,Integer>>(orimap.entrySet());
    	
    	Collections.sort(entryList,
    			new Comparator<Map.Entry<String,Integer>>(){

					@Override
					public int compare(Entry<String, Integer> o1,
							Entry<String, Integer> o2) {
						// TODO Auto-generated method stub
						int value1 = 0,value2 = 0;
						try {
							value1 = o1.getValue();
							value2 = o2.getValue();
						} catch (NumberFormatException e) {
							// TODO: handle exception
							value1 = 0;
							value2 = 0;
						}
						return value2 - value1;
					}
    	});
    	Iterator<Map.Entry<String, Integer>> iterator = entryList.iterator();
    	
    	Map.Entry<String, Integer> tmpEntry = null;
    	while (iterator.hasNext()) {

    		tmpEntry = iterator.next();
    		sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
		}
    	return sortedMap;
    }
    
    /**
     *  判断字符串是否全部匹配关键字
     * @param string
     * @param keywords
     * @return
     */
    public static boolean isMaxMatch(String string,String[] keywords){
    	if(string == "" || keywords == null || keywords.length == 0){
    		return false;
    	}
    	
    	for(String key : keywords){
    		if(!string.matches(".*" + key + ".*")){
    			return false;
    		}
    	}
    	return true;
    }
	
    
	
    /**
     *  convert array to list
     * @param arrays
     * @return
     */
    public static List<String> arrayToList(String[] arrays){
    	if(arrays == null || arrays.length == 0){
    		return null;
    	}
    	int length = arrays.length;
    	List<String> results = new ArrayList<String>(length);
    	
    	for( int i = 0; i < length; i++ ){
    		results.add(arrays[i].trim());
    	}
    	
    	return results;
    }
    
    /**
     *  remove some items of map that in the list
     * @param maps
     * @param list
     * @return
     */
    public static Map<String, Integer> removeMapInList(Map<String,Integer> maps,List<String> list){
    	if(maps == null || maps.isEmpty()){
    		return null;
    	}
    	// no need to remove
    	if(list == null || list.isEmpty()){
    		return maps;
    	}
    	// remove some items of map in list
    	for(String s : list){
    		maps.remove(s);
    	}
    	return maps;
    }
}
