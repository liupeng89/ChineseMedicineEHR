package com.um.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.Set;

import com.um.classify.CMDescriptionClassify;
import com.um.classify.CWRelationMapping;
import com.um.classify.DiagnosticsClassify;
import com.um.data.DataBaseSetting;
import com.um.data.DiagClassifyData;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.EhealthRecordConverter;
import com.um.mongodb.converter.MedicineStatics;

public class DiagMedicineProcess {
	
	
	/**
	 *  返回某一批次的病历
	 *  
	 * @param batch
	 * @param allEHealthRecords
	 * @return
	 */
	public static List<EHealthRecord> getRecordsByBatch(String batch,List<EHealthRecord> allEHealthRecords){
		if(batch == "" || batch.equals("") || batch == "null" ){
			return allEHealthRecords;
		}
		
		List<EHealthRecord> results = new ArrayList<EHealthRecord>();
		
		for( EHealthRecord e : allEHealthRecords ){
			if(e.getBatchString().equals(batch) || e.getBatchString() == batch){
				results.add(e);
			}
		}
		
		return results;
	}
	
	
	/**
	 *  查询批次
	 * @return
	 */
	public static List<String> getBatch(){
		List<EHealthRecord> eHealthRecords = CWRelationMapping.queryEhealthDataByCollection(DataBaseSetting.ehealthcollection);
		
		Set<String> batchSet = new HashSet<String>();
		
		for(EHealthRecord e : eHealthRecords){
			batchSet.add(e.getBatchString());
		}
		List<String> resultList = new ArrayList<String>();
		for(String s : batchSet){
			if(s.contains(".")){
				resultList.add(s.substring(0, 4).trim());
			}else{
				resultList.add(s);
			}
		}
		Collections.sort(resultList);
		Collections.reverse(resultList);
		
		return resultList;
	}
	
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
	 *   根据描述对病例进行分类
	 * @param ehealthMap
	 * @return
	 */
	public static Map<String, ArrayList<EHealthRecord>> getClassfyByDescription(Map<EHealthRecord, String> ehealthMap){
		if(ehealthMap == null || ehealthMap.isEmpty()){
			return null;
		}
		Map<String, ArrayList<EHealthRecord>> resultMap = new HashMap<String, ArrayList<EHealthRecord>>();
		
		// 1.构造参照表
		Map<String, HashMap<String,ArrayList<String>>> keyMap = creatrReference();
		
		// 2、查询参照表,统计病例
		Set<EHealthRecord> keywordSet = ehealthMap.keySet();
		Set<String> projectSet = keyMap.keySet();
		Map<EHealthRecord, String> recordMap = new HashMap<EHealthRecord, String>(); // 病例－－－关键字
		for(EHealthRecord e : keywordSet){
			String conditionString = e.getConditionsdescribed(); // 病症描述
			String keyString = "";
			// 3.返回结果
			for(String p : projectSet){
				Set<String> descSet = keyMap.get(p).keySet();
				for(String d : descSet){
					ArrayList<String> descList = keyMap.get(p).get(d);
					if(descList != null && descList.size() > 0 ){
						for(String s : descList ){
							if(conditionString.matches(".*" + s + ".*")){
								keyString += "|" + p + "|"  +d ;
							}
						}
					}
				}
			}
			recordMap.put(e, keyString);
		}
		
		//3. 统计
		
		Set<String> codeSet = new HashSet<String>();
		Set<EHealthRecord> eSet = recordMap.keySet();
		for(EHealthRecord e : eSet){
			if(codeSet.add(recordMap.get(e))){
				// 还没统计
				ArrayList<EHealthRecord> eList = new ArrayList<EHealthRecord>();
				eList.add(e);
				String string = recordMap.get(e);
				resultMap.put(string, eList);
			}else{
				// 已经统计了
				String des = recordMap.get(e);
				ArrayList<EHealthRecord> elist = resultMap.get(des);
				elist.add(e);
				resultMap.remove(des);
				resultMap.put(des, elist);
			}
		}
//		Set<String> esSet = resultMap.keySet();
//		Map<String, Integer> meMap = null;
//		for(String s : esSet){
////			if(resultMap.get(s).size() > 5){
////				meMap = statisEhealthMedicine(resultMap.get(s));
////				System.out.println(s + ":" + resultMap.get(s).size() + "----" + meMap);
////			}
//			meMap = statisEhealthMedicine(resultMap.get(s));
////			Set<String> keSet  = meMap.keySet();
////			for(String ss : keSet){
////				int count = meMap.get(ss);
////				if(count > resultMap.get(s).size() * 0.8 && resultMap.get(s).size() > 4){
////					System.out.println(s + ":" + resultMap.get(s).size() + "----" + meMap);
////				}
////			}
//				
//		}
		
		return resultMap;
	}
	
	/**
	 * 构建关键字参照表
	 * @return
	 */
	public static Map<String, HashMap<String,ArrayList<String>>> creatrReference(){
		// 1.构造参照表
		String[] keywords = DiagClassifyData.cndescriptionkeywords;
		Map<String, HashMap<String,ArrayList<String>>> keyMap = new HashMap<String, HashMap<String,ArrayList<String>>>();
		for(String key : keywords){
			String[] projects = key.split("%");// 0:部位 1:描述
			String[] descriptions = projects[1].split("#"); //不同描述
			HashMap<String, ArrayList<String>> descMap = new HashMap<String, ArrayList<String>>();
			for(String s : descriptions){
				String[] desc = s.split(":");
//						System.out.println(desc[1]);
				String[] descKey = desc[1].split("\\|");
				ArrayList<String> descList = (ArrayList<String>) DiagMedicineProcess.arrayToList(descKey);
				descMap.put(desc[0], descList);
			}
			keyMap.put(projects[0], descMap);
		}
		return keyMap;
	}
	
	/**
	 * 构建关键字参照表
	 * @return
	 */
	public static Map<String, HashMap<String,ArrayList<String>>> creatrReference(String[] keywords){
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
	public static Set<String> getMedicinesByDesc(String description,List<EHealthRecord> eHealthRecords){
		if(description == "" || eHealthRecords == null || eHealthRecords.isEmpty()){
			return null;
		}
		// 1. 根据输入描述，生成关键字  编码
		Map<String, String> descriptionCode = getDescritionCode(description); //描述关键字编码
		
		// 2. 根据编码，识别病历中的关键字，并对他们进行分类
		List<EHealthRecord> eRecords = new ArrayList<EHealthRecord>(); // 同种描述的病历
		for(EHealthRecord e : eHealthRecords){
			if(e.getConditionsdescribed() == ""){
				continue;
			}
			String descString = e.getConditionsdescribed();
			Map<String, String> codeMap = getDescritionCode(descString);//每一病历描述的关键字编码
			if(checkCode(descriptionCode, codeMap)){
				eRecords.add(e);
			}
		}
		if(eRecords.size() == 0){
			return null;
		}
		// 3. 统计中药
		Map<String, Integer> medicineMap = statisEhealthMedicine(eRecords);
		medicineMap = DiagMedicineProcess.sortMapByValue(medicineMap); //排序！！
		
		Set<String> medicineSet = medicineMap.keySet();
//		medicineList.addAll(medicineSet);
		return medicineSet;
	}
	
	/**
	 * 根据描述，得出中药处方
	 * @param description
	 * @param eHealthRecords
	 * @return
	 */
	public static Set<String> getMedicinesByDescription(String description,List<EHealthRecord> eHealthRecords){
		if( description == "" || description.equals("") || eHealthRecords == null || eHealthRecords.size() == 0){
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
	 * 根据描述，选择相似病历
	 * @param description
	 * @param eHealthRecords
	 * @return
	 */
	public static List<EHealthRecord> getRecordByDescription(String description,List<EHealthRecord> eHealthRecords){
		if( description == "" || description.equals("") || eHealthRecords == null || eHealthRecords.size() == 0){
			return null;
		}
		
		// 1. 处理description
		String[] descriptionSplits = description.split(",");
		if( descriptionSplits == null || descriptionSplits.length == 0 ){
			return null;
		}
		
		Set<String> descriptionSet = new HashSet<String>(); // 输入描述中的关键字
		for( String string : descriptionSplits ){
			descriptionSet.add(string);
		}
		
		// 2. 根据关键字表，生成description的关键字编码
		Map<String, String> codeTableMap = MedicineByDescription.convertArraysToMap(DiagClassifyData.descKeywords);
		Map<String, String[]> descMap = new HashMap<String,String[]>();
		
		Set<String> codeTableSet = codeTableMap.keySet(); // 全部的关键字
		// 全部的 项目 和 关键字数组
		for( String code : codeTableSet ){
			String valueString = codeTableMap.get(code);
			String[] splits = valueString.split("\\|");
			if( splits == null || splits.length == 0 ){
				continue;
			}
			descMap.put(code, splits);
		}
		
		Map<String, String> descriptionCodeMap = new HashMap<String, String>(); // 项目：0｜1
		// 全部的项目，有则为1 ， 无则为0
		for( String string : codeTableSet ){
			if( descriptionSet.contains(string) ){
				descriptionCodeMap.put(string, "1");
			}else{
				descriptionCodeMap.put(string, "0");
			}
		}
		
//		System.out.println(descriptionCodeMap);
		
		
		// 4. 扫描全部的病例，根据每一个病例，生成对应的编码
		List<EHealthRecord> eRecords = new ArrayList<EHealthRecord>(); // 同种描述的病历
		
		for( EHealthRecord e : eHealthRecords ){
			if( e.getConditionsdescribed() == "" || e.getConditionsdescribed().equals("") ){
				continue;
			}
			String descString = e.getConditionsdescribed(); // 描述
			Map<String, String> ehealthMap = new HashMap<String, String>();
			for( String string : codeTableSet ){
				String[] keywords = descMap.get(string);
				if( checkDescriptionMatch(descString,keywords) ){
					ehealthMap.put(string, "1");
				}else{
					ehealthMap.put(string, "0");
				}
			}
			
			if( checkMap(descriptionCodeMap, ehealthMap)){
				// add the privacy
				e = EhealthRecordConverter.protectPatientInfo(e);
				eRecords.add(e);
			}
		}
		
		if( eRecords.size() == 0 ){
			System.out.println("erecords is null");
			return null;
		}
		return eRecords;
	}
	
	/**
	 *  根据 证型 ＋ 症状，获取相似病例
	 * @param description
	 * @param eHealthRecords
	 * @return
	 */
	public static List<EHealthRecord> getEhealthRecordByDescription(String description,List<EHealthRecord> eHealthRecords){
		if( description == "" || description.equals("") || eHealthRecords == null || eHealthRecords.size() == 0){
			return null;
		}
		List<EHealthRecord> similarRecords = new ArrayList<EHealthRecord>(); // 同种描述的病历
		// 1. 处理description
		String[] descriptionSplits = description.split(",");
		if( descriptionSplits == null || descriptionSplits.length == 0 ){
			return null;
		}
		System.out.println("desc: " + description);
		
		// 2. 生成关键字编码表  <部位， < 状态：［k1,k2,k3.....］>>
		Map<String, HashMap<String, ArrayList<String>>> keywordCodeMap = creatrReference(DiagClassifyData.descriptionKeywords);
		Map<String, String> normalTableMap = MedicineByDescription.convertArraysToMap(DiagClassifyData.normalAndBaddescription);
		// 去掉正常的status
		
		Set<String> descriptionSet = new HashSet<String>(); // 输入描述中的关键字
		for( String string : descriptionSplits ){
			if (normalTableMap.get(string)!=null && !normalTableMap.get(string).equals("0")) {
				descriptionSet.add(string);
			}
		}
		if (descriptionSet == null || descriptionSet.size() == 0) {
			return null;
		}
		System.out.println(descriptionSet);
		// 3. 输入编码
		Map<String, ArrayList<String>> inputCodeMap = new HashMap<String, ArrayList<String>>();
		Set<String> projectKey = keywordCodeMap.keySet();
		for (String project : projectKey) {
			// 
			Set<String> statusKeySet = keywordCodeMap.get(project).keySet();
			if( statusKeySet == null || statusKeySet.size() == 0 ){
				continue;
			}
			for (String inString : descriptionSet) {
				if (keywordCodeMap.get(project).get(inString) != null && !keywordCodeMap.get(project).get(inString).contains("无")) {
					ArrayList<String> contentList = keywordCodeMap.get(project).get(inString);
					inputCodeMap.put(inString, contentList);
				}
			}
		}
		System.out.println("input code: " + inputCodeMap.toString());
		
		Set<String> keySet = inputCodeMap.keySet();

		if (keySet.contains("cmtreat")||keySet.contains("shuqian")||keySet.contains("shuhou")||
				keySet.contains("zhiliaozhong")||
				keySet.contains("zhiliaohou")||
				keySet.contains("hualiaozhong")||
				keySet.contains("hualiaohou")||
				keySet.contains("fenzi")||
				keySet.contains("mianyi")) {
			
		}
		if (keySet.contains("cmtreat")) {
			inputCodeMap.remove("cmtreat");
		}
		if (keySet.contains("shuqian")) {
			inputCodeMap.remove("shuqian");
		}
		if (keySet.contains("shuhou")) {
			inputCodeMap.remove("shuhou");
		}
		if (keySet.contains("zhiliaozhong")) {
			inputCodeMap.remove("zhiliaozhong");
		}
		if (keySet.contains("zhiliaohou")) {
			inputCodeMap.remove("zhiliaohou");
		}
		if (keySet.contains("hualiaozhong")) {
			inputCodeMap.remove("hualiaozhong");
		}
		if (keySet.contains("hualiaohou")) {
			inputCodeMap.remove("hualiaohou");
		}
		
		if (keySet.contains("fenzi")) {
			inputCodeMap.remove("fenzi");
		}
		if (keySet.contains("mianyi")) {
			inputCodeMap.remove("mianyi");
		}
		
		// 4. 匹配
		for (EHealthRecord eHealthRecord : eHealthRecords) {
			int statusmatchnum = 0;
			System.out.println("--------------------");
			Set<String> statusSet = inputCodeMap.keySet();
			
			for (String status : statusSet) {
				int contentmatchnum = 0;
				ArrayList<String> contentList = inputCodeMap.get(status);
				for (String c : contentList) {
					if (eHealthRecord.getConditionsdescribed().contains(c)) {
						System.out.println("c: " + c);
						statusmatchnum++;
						break;
					}
					contentmatchnum++;
				}
				if (contentmatchnum == contentList.size()-1) {
					// 该状态的关键字全部都不一致，则该病例不属于相似病例
					break;
				}
			}
			System.out.println("status num: " + statusmatchnum);
			if (statusmatchnum >= (statusSet.size() / 2 + 1)) {
				similarRecords.add(eHealthRecord);
			}
		}
		
		System.out.println("sim num: " + similarRecords.size());
		
//		int matchnum = 0;
//		int maxmatchnum = 0;
		
//		Set<String> keySet = inputCodeMap.keySet();
//		if (keySet.contains("badsleep") || keySet.contains("worsesleep") || keySet.contains("worstsleep") || keySet.contains("somnolencesleep")) {
//			maxmatchnum++;
//		}
//		if (keySet.contains("redlittlesputumcolor") || keySet.contains("redmuchsputumcolor") || keySet.contains("redmoresputumcolor") ) {
//			maxmatchnum++;
//		}
//		if (keySet.contains("okxonglei") || keySet.contains("badxonglei") || keySet.contains("worsexonglei") ) {
//			maxmatchnum++;
//		}
//		if (keySet.contains("badna") || keySet.contains("anorexiana") || keySet.contains("worsena") ) {
//			maxmatchnum++;
//		}
//		if (keySet.contains("badsleep") || keySet.contains("worsesleep") || keySet.contains("worstsleep") ) {
//			maxmatchnum++;
//		}
//		
//		System.out.println("maxmatch: " + matchnum);

		
		
		
		// 3. 根据输入，确定输入编码
//		Map<String, HashMap<String, String>> inputDescCodeMap = new HashMap<String, HashMap<String,String>>();
//		Set<String> projectKeySet = keywordCodeMap.keySet();
//		// project
//		for( String project : projectKeySet ){
//			// status
//			Set<String> statusKeySet = keywordCodeMap.get(project).keySet();
//			if( statusKeySet == null || statusKeySet.size() == 0 ){
//				continue;
//			}
//			HashMap<String, String> inputStatusMap = new HashMap<String, String>(); 
//			for( String status : statusKeySet ){
//				if( descriptionSet.contains(status) ){
//					inputStatusMap.put(status, "1");
//				}else{
//					inputStatusMap.put(status, "0");
//				}
//			}
//			inputDescCodeMap.put(project, inputStatusMap);
//		}
//		// 主要症状和次要症状
//		String[] mainDescriptionStrings = DiagClassifyData.mainDescriptionStrings; // 主要症状
//		String[] seconddescriptionStrings = DiagClassifyData.seconddescriptionStrings; // 次要症状
//		
//		// 4. 匹配病例
//		for( EHealthRecord ehHealthRecord : eHealthRecords ){
//			// 4.1 每一个病例对应的编码
//			Map<String, HashMap<String, String>> eRecrodCodeMap =  getRecordDescCodeMap(ehHealthRecord);
//			// 4.2 判断是匹配---判断方法：
//			/*
//			 * 判断方法:1. 主要症状需要尽量相同
//			 * 			2. 次要症状并集相同
//			 * 	
//			 */
//			Map<String, String> mainMatchMap = new HashMap<String, String>();
//			Map<String, String> secondMatchMap = new HashMap<String, String>();
//			
//			// 4.3 检测主要症状匹配情况---1:匹配  0: 不匹配
//			for( String m : mainDescriptionStrings ){
//				if( checkProjectMatch(inputDescCodeMap.get(m), eRecrodCodeMap.get(m))){
//					mainMatchMap.put(m, "1");
//				}else{
//					mainMatchMap.put(m, "0");
//				}
//			}
//			
//			// 4.4 检测次要症状匹配情况
//			for( String m : seconddescriptionStrings ){
//				if( checkProjectMatch(inputDescCodeMap.get(m), eRecrodCodeMap.get(m))){
//					secondMatchMap.put(m, "1");
//				}else{
//					secondMatchMap.put(m, "0");
//				}
//			}
//			
//			// 4.4 根据主要症状和次要症状的匹配情况，确定病例是否符合条件
//			/*
//			 * 临时条件：
//			 * 			逐次递减判断条件的个数，直至符合病例数出现
//			 * 			
//			 */
//			
//			for(int i = 0; i < 8; i++){
//				
//				int countMain = 0;
//				int countSecond = 0;
//				Set<String> mainSet = mainMatchMap.keySet();
//				Set<String> secondSet = secondMatchMap.keySet();
//				for(String m : mainSet){
//					if(mainMatchMap.get(m).equals("1")){
//						countMain++;
//					}
//				}
//				for(String s : secondSet){
//					if(secondMatchMap.get(s).equals("1")){
//						countSecond++;
//					}
//				}
//				if(countMain >=  9 - i && countSecond >= 8 - i){
//					// 该病例符合条件
//					similarRecords.add(ehHealthRecord);
//				}
//				// 至少为5个病例
//				if( similarRecords.size() >= 5 ){
//					break;
//				}
//			}
//		}
		return similarRecords;
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
	 *  判断编码是否相同
	 * @param code1
	 * @param code2
	 * @return
	 */
	public static boolean checkCode(Map<String, String> code1,Map<String, String> code2){
		if(code1 == null || code2 == null || code1.isEmpty() || code2.isEmpty()){
			return false;
		}
		boolean flag = false;
		
		int maxMatchNum = (int) (code1.size() * 0.8);
		Set<String> codeSet = code1.keySet();
		Map<String, String> matchResult = new HashMap<String, String>();
		for(String s : codeSet){
			if(code1.get(s).equals(code2.get(s))){
				matchResult.put(s, code1.get(s));
			}
		}
		if(matchResult.size() >= maxMatchNum){
			flag = true;
		}else{
			flag = false;
		}
		return flag;
	}
	
	/**
	 *  判断一个部位 的症状描述是否相同
	 * @param h1
	 * @param h2
	 * @return
	 */
	public static boolean checkProjectMatch(HashMap<String, String> h1, HashMap<String, String> h2){
		if(h1 == null || h2 == null || h1.size() != h2.size()){
			return false;
		}
		Set<String> keySet1 = h1.keySet();
		for( String k : keySet1 ){
			if(!h2.get(k).equals(h1.get(k))){
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 *  
	 * @param map1
	 * @param map2
	 * @return
	 */
	public static boolean checkMap(Map<String, String> map1,Map<String, String>map2){
		if( map1 == null || map1.size() == 0 || map2 == null || map2.size() == 0 ){
			return false;
		}
		Set<String> key1 = map1.keySet();
		Set<String> key2 = map2.keySet();
		
		if( !key1.containsAll(key2) ){
			return false;
		}
		for( String k : key1 ){
			if( !map1.get(k).equals(map2.get(k))){
				return false;
			}
		}
		return true;
	}
	
	/**
	 *  根据描述，生成描述关键字编码
	 * @param description
	 * @return
	 */
	public static Map<String, String> getDescritionCode(String description){
		if(description == ""){
			return null;
		}
		Map<String, String> descriptionCode = new HashMap<String, String>();
		//1， 读取关键字表---<部位 ， < 状态， [k1,k2,k3......] > >
		Map<String, HashMap<String, ArrayList<String>>> descriptionTableMap = getDescriptionMap(); //描述关键字列表
		
		Set<String> project = descriptionTableMap.keySet();
		HashMap<String, ArrayList<String>> desHashMap = null;
		for(String descString : project){
			String valueString = "good";
			desHashMap = descriptionTableMap.get(descString);
			Set<String> desSet = desHashMap.keySet();
			for(String s:desSet){
				ArrayList<String> keywordList = desHashMap.get(s);
				for(String key: keywordList){
					if(description.matches(".*" + key + ".*")){
						// 匹配
						valueString = s;
						break;
					}
				}
			}
			descriptionCode.put(descString, valueString);
		}
		
		return descriptionCode;
	}
	
	/**
	 *  根据描述，生成描述关键字编码
	 * @param description
	 * @return
	 */
	public static Map<String, String> getDescritionCode(String description,String[] descriptionStrings){
		if(description == ""){
			return null;
		}
		Map<String, String> descriptionCode = new HashMap<String, String>();
		//1， 读取关键字表---<部位 ， < 状态， [k1,k2,k3......] > >
		Map<String, HashMap<String, ArrayList<String>>> descriptionTableMap = getDescriptionMap(descriptionStrings); //描述关键字列表
		
		Set<String> project = descriptionTableMap.keySet();
		HashMap<String, ArrayList<String>> desHashMap = null;
		for(String descString : project){
			String valueString = "good";
			desHashMap = descriptionTableMap.get(descString);
			Set<String> desSet = desHashMap.keySet();
			for(String s:desSet){
				ArrayList<String> keywordList = desHashMap.get(s);
				for(String key: keywordList){
					if(description.matches(".*" + key + ".*")){
						// 匹配
						valueString = s;
						break;
					}
				}
			}
			descriptionCode.put(descString, valueString);
		}
		
		return descriptionCode;
	}
	
	/**
	 *  初始化描述对比表
	 *  	<部位， <状态， [k1,k2,k3.....]>>
	 * @return
	 */
	public static Map<String, HashMap<String, ArrayList<String>>> getDescriptionMap(){
		String[] descriptionStrings = DiagClassifyData.cndescriptionkeywords1;
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
	 *  根据描述，基于病例关键字，返回对应的中药处方---按照关键字的交集查找
	 * @param descString
	 * @param eHealthRecords
	 * @return
	 */
	public static Set<String> getMedicinesByDescriptionMix(String descString,List<EHealthRecord> eHealthRecords){
		if(descString == "" || eHealthRecords == null || eHealthRecords.size() == 0){
			return null;
		}
		
		// 根据中医描述，提取全部病例的中医描述关键字
		List<String> cnkeywords = DiagMedicineProcess.arrayToList(DiagClassifyData.cndescriclassify); // 中医描述关键字list
		// 3.1、根据中医描述关键字，对病例分类
		Map<EHealthRecord, String> ehealthMap = DiagMedicineProcess.classifyEhealthMap(cnkeywords, eHealthRecords);
		List<CMDescriptionClassify> cmDescList = new ArrayList<CMDescriptionClassify>(); // 分类列表
		
		//Todo   病症描述，对应的中药统计
//		Map<String, ArrayList<EHealthRecord>> rMap = DiagMedicineProcess.getClassfyByDescription(ehealthMap);
		
		Set<String> descCodeSet = new TreeSet<String>(); // 编码集合
		Set<EHealthRecord> sets = ehealthMap.keySet();
		CMDescriptionClassify cm = null;
		String[] strings = null;
		
		for(EHealthRecord e: sets){
			if(descCodeSet.add(ehealthMap.get(e))){
				// 新的编码类型
				cm = new CMDescriptionClassify();
				strings = ehealthMap.get(e).split("\\|");
				cm.setDescriptionString(strings[0]);
				cm.setDescriptionCode(strings[1]);
				cm.setKeywords(DiagMedicineProcess.stringToArray(strings[0]));
				cm.geteHealthRecords().add(e);
				
				cmDescList.add(cm);
			}else{
				// set中已经有了该类型的编码
				strings = ehealthMap.get(e).split("\\|");
				if(cmDescList != null && cmDescList.size() > 0){
					for(CMDescriptionClassify c : cmDescList){
						if(c.getDescriptionCode().equals(strings[1])){
							c.geteHealthRecords().add(e);
						}
					}
				}
			}
		}
		
		// 3.2. 识别输入文字，确定分类类型
		CMDescriptionClassify cmDescriptionClassify = DiagMedicineProcess.matchDescriptionClassify(descString, cmDescList);// 确定描述分类
		if(cmDescriptionClassify == null){
			return null;
		}
		
		// 3.3. 统计该类型中的中药处方---统计属于该类型的病历中的所有中药处方情况
		Map<String, Integer> cnmedicineMap = new HashMap<String, Integer>();		
		if(cmDescriptionClassify != null && cmDescriptionClassify.geteHealthRecords() != null &&
				cmDescriptionClassify.geteHealthRecords().size() > 0){
			cnmedicineMap = DiagMedicineProcess.statisMedicineWithCMDescription(cmDescriptionClassify);
		}
		
		// 3.4. 输出中药处方
		Set<String> cnmedicienSet = cnmedicineMap.keySet(); // 统计与输入描述相符合类型的中药名称
		return cnmedicienSet;
	}
	
	
	/**
	 *  根据描述，基于病例关键字，返回对应的中药处方---按照关键字的并集查找
	 * @param descString
	 * @param eHealthRecords
	 * @return
	 */
	public static Set<String> getMedicinesByDescriptionUnion(String descString,List<EHealthRecord> eHealthRecords){
		if(descString == "" || eHealthRecords.isEmpty() || eHealthRecords == null){
			return null;
		}
		Set<String> medicineSet = new HashSet<String>(); // 中药名称
		//1.根据描述，构建关键字list
		List<String> keywordList = new ArrayList<String>(); // 关键字list
		String[] cnkeywordsList = DiagClassifyData.cndescriclassify;
		String[] splitStrings = null;
		for(String s : cnkeywordsList){
			splitStrings = s.split("\\|");
			if(descString.matches(".*"+splitStrings[0].trim()+".*")){
				keywordList.add(splitStrings[0].trim());
			}
		}
		if(keywordList.isEmpty()){
			return null;
		}
		//2.分别查找有此关键字的病例的中药处方的并集
		Map<String, HashMap<String, Integer>> keyMap = new HashMap<String, HashMap<String,Integer>>();
		for(String keyword : keywordList){
			List<String> medicineList = new ArrayList<String>(); // 某一关键字对应的全部中药
			for(EHealthRecord e : eHealthRecords){
				if(e.getConditionsdescribed().matches(".*" + keyword + ".*") && e.getChineseMedicines() != null && 
						!e.getChineseMedicines().isEmpty()){
					for(ChineseMedicine c : e.getChineseMedicines()){
						medicineList.add(c.getNameString());
					}
				}
			}
			keyMap.put(keyword, MedicineStatics.staticsChineseMedicine(medicineList));
		}
		//3. 对统计结果进行分析汇总，整理
		
		// 每一种症状，取前15种中药
		Set<String> descriptionKeySet = keyMap.keySet();
		int count = 15;
		for(String key : descriptionKeySet){
			Set<String> medicSet = keyMap.get(key).keySet();
			if(medicSet.size() > count){
				int index = 0;
				for(String s : medicSet){
					if(index < count){
						medicineSet.add(s);
						index++;
					}else{
						break;
					}
				}
			}else{
				medicineSet.addAll(medicSet);
			}
		}
		
		//4.返回结果
		return medicineSet;
	}
	
	
	/**
	 *  对中医病症描述进行分解
	 * @param eRecord
	 * @return
	 */
	public static Map<String, HashMap<String,String>> getConditionDescription(EHealthRecord eRecord){
		if(eRecord == null || eRecord.getConditionsdescribed() == ""){
			return null;
		}
		Map<String, HashMap<String,String>> resultMap = new HashMap<String, HashMap<String,String>>();
		// 1.构造参照表
		String[] keywords = DiagClassifyData.cndescriptionkeywords;
		Map<String, HashMap<String,ArrayList<String>>> keyMap = new HashMap<String, HashMap<String,ArrayList<String>>>();
		for(String key : keywords){
			String[] projects = key.split("%");// 0:部位 1:描述
			String[] descriptions = projects[1].split("#"); //不同描述
			HashMap<String, ArrayList<String>> descMap = new HashMap<String, ArrayList<String>>();
			for(String s : descriptions){
				String[] desc = s.split(":");
//				System.out.println(desc[1]);
				String[] descKey = desc[1].split("\\|");
				ArrayList<String> descList = (ArrayList<String>) DiagMedicineProcess.arrayToList(descKey);
				descMap.put(desc[0], descList);
			}
			keyMap.put(projects[0], descMap);
		}
//		System.out.println(keyMap);
		// 2. 解析
		String conditionString = eRecord.getConditionsdescribed(); // 病症描述
		// 3.返回结果
		Set<String> projectSet = keyMap.keySet();
		for(String p : projectSet){
			HashMap<String, String> descHashMap = new HashMap<String, String>();
			Set<String> descSet = keyMap.get(p).keySet();
			for(String d : descSet){
				ArrayList<String> descList = keyMap.get(p).get(d);
				if(descList != null && descList.size() > 0 ){
					for(String s : descList ){
						if(conditionString.matches(".*" + s + ".*")){
							descHashMap.put(d, s);
						}
					}
				}
			}
			resultMap.put(p, descHashMap);
		}
		return resultMap;
	}
	
	/**
	 *  对中医病症描述进行分解
	 * @param eRecord
	 * @return
	 */
	public static Map<String, HashMap<String,String>> getRecordDescCodeMap(EHealthRecord eRecord){
		if(eRecord == null || eRecord.getConditionsdescribed() == ""){
			return null;
		}
		Map<String, HashMap<String,String>> resultMap = new HashMap<String, HashMap<String,String>>();
		// 1.构造参照表  生成关键字编码表  <部位， < 状态：［k1,k2,k3.....］>>
		Map<String, HashMap<String, ArrayList<String>>> keyMap = creatrReference(DiagClassifyData.descriptionKeywords);
//		String[] keywords = DiagClassifyData.descriptionKeywords;
//		Map<String, HashMap<String,ArrayList<String>>> keyMap = new HashMap<String, HashMap<String,ArrayList<String>>>();
//		for(String key : keywords){
//			String[] projects = key.split("%");// 0:部位 1:描述
//			String[] descriptions = projects[1].split("#"); //不同描述
//			HashMap<String, ArrayList<String>> descMap = new HashMap<String, ArrayList<String>>();
//			for(String s : descriptions){
//				String[] desc = s.split(":");
//				String[] descKey = desc[1].split("\\|");
//				ArrayList<String> descList = (ArrayList<String>) DiagMedicineProcess.arrayToList(descKey);
//				descMap.put(desc[0], descList);
//			}
//			keyMap.put(projects[0], descMap);
//		}
		// 2. 解析
		String conditionString = eRecord.getConditionsdescribed(); // 病症描述
		// 3.返回结果
		Set<String> projectSet = keyMap.keySet();
		for( String project : projectSet ){
			Set<String> statusSet = keyMap.get(project).keySet();
			if( statusSet == null || statusSet.size() == 0 ){
				continue;
			}
			HashMap<String, String> statusCodeMap = new HashMap<String, String>();//
			for( String status : statusSet ){
				ArrayList<String> statusKeyWords = keyMap.get(project).get(status);
				if( statusKeyWords == null || statusKeyWords.size() == 0 ){
					continue;
				}
				int index = 0;
				for( String s : statusKeyWords ){
					if( conditionString.matches( ".*" + s + ".*" )){
						statusCodeMap.put(status, "1");
						break;
					}
					if( index == statusKeyWords.size() - 1 ){
						statusCodeMap.put(status, "0");
					}
					index++;
				}
			}
			resultMap.put(project, statusCodeMap);
		}
		
		return resultMap;
	}
	/**
	 *  对中医病症描述进行分解
	 * @param eRecord
	 * @return
	 */
	public static Map<String, HashMap<String,String>> getConditionDescriptionByDesc(String description){
		if(description == ""){
			return null;
		}
		Map<String, HashMap<String,String>> resultMap = new HashMap<String, HashMap<String,String>>();
		// 1.构造参照表
		String[] keywords = DiagClassifyData.cndescriptionkeywords;
		Map<String, HashMap<String,ArrayList<String>>> keyMap = new HashMap<String, HashMap<String,ArrayList<String>>>();
		for(String key : keywords){
			String[] projects = key.split("%");// 0:部位 1:描述
			String[] descriptions = projects[1].split("#"); //不同描述
			HashMap<String, ArrayList<String>> descMap = new HashMap<String, ArrayList<String>>();
			for(String s : descriptions){
				String[] desc = s.split(":");
//				System.out.println(desc[1]);
				String[] descKey = desc[1].split("\\|");
				ArrayList<String> descList = (ArrayList<String>) DiagMedicineProcess.arrayToList(descKey);
				descMap.put(desc[0], descList);
			}
			keyMap.put(projects[0], descMap);
		}
//		System.out.println(keyMap);
		// 2. 解析
		// 3.返回结果
		Set<String> projectSet = keyMap.keySet();
		for(String p : projectSet){
			HashMap<String, String> descHashMap = new HashMap<String, String>();
			Set<String> descSet = keyMap.get(p).keySet();
			for(String d : descSet){
				ArrayList<String> descList = keyMap.get(p).get(d);
				if(descList != null && descList.size() > 0 ){
					for(String s : descList ){
						if(description.matches(".*" + s + ".*")){
							descHashMap.put(d, s);
						}
					}
				}
			}
			resultMap.put(p, descHashMap);
		}
		return resultMap;
	}
	
	/**
	 * 	统计出现概率大约 percent的中药
	 * 
	 * @param medicines----- 中药处方统计结果<名称，数量>
	 * @param length-------- 全部病历数量
	 * @param percent------- 百分比
	 * @return
	 */
	public static List<String> statisMedicineByProbability(Map<String, Integer> medicines,int length,double percent){
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
	 *  统计一组出现概率的并集 大于percent的中药
	 * @param medicineMap   ----- 中药统计
	 * @param length    --------- 病历数量
	 * @param percent   --------- 出现概率
	 * @return： 多个中药名称        
	 */
	public static List<String> accumulateMedicines(Map<String, Integer> medicineMap,List<EHealthRecord> allList,double percent){
		if(medicineMap == null || medicineMap.isEmpty() || allList == null || allList.isEmpty()){
			return null;
		}
		List<String> results = new ArrayList<String>(); // 结果
		Set<String> medicineNameSet = medicineMap.keySet(); // 当前全部的中药名称
		
		// 根据某一组中药的出现概率的并集是否大于100%来判断这组中药是否可以作为结果输出
		
		List<String> accumuMedicineList = new ArrayList<String>(); //中药分组
		
		Iterator<String> iterator = medicineNameSet.iterator();
		while(iterator.hasNext()){
			// 1. 构建新的分组
			accumuMedicineList.add(iterator.next()); // 构建分组
			// 2. 判断该分组是否符合条件 : 并集的数量 >= allList.size()
			// 		2.1 计算并集的数量
			int union = EhealthRecordMath.getUnion(accumuMedicineList, allList); // 求中药分组的并集（符合条件的病例数）
			// 		2.2 判断 ：并集的数量 >= allList.size()
			if(union >= allList.size() * percent){
				results = accumuMedicineList;
				break;
			}
			// 3. 若符合，则结束循环，输出
			// 4. 不符合，继续循环，构建分组
		}
		return results;
	}
	
	/**
	 *  统计中药出现的概率
	 * 		主要功能：
	 * 				1、输入单味中药时，只算出现数量和概率
	 * 				2、输入多味中药时，计算出现概率的交集和并集
	 * @param medicines
	 * @return
	 */
	public static Map<String, String> statisMedicProbability(String medicines){
		if(medicines == ""){
			return null;
		}
		Map<String, String> resultMap = new HashMap<String, String>();
		// 1. 拆分中药名称
		
		String[] names = medicines.split(" ");
		if(names == null || names.length == 0){
			return null;
		}
		// 2、获取全部病历数据
		List<EHealthRecord> allRecrods = CWRelationMapping.queryEhealthData();
		
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
//		// 2、获取全部病历数据
//		CWRelationMapping cMapping = new CWRelationMapping();		
//		List<EHealthRecord> allRecrods = cMapping.queryEhealthData();
		
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
	 *     统计中药出现的概率----- 带有批次
	 * 		主要功能：
	 * 				1、输入单味中药时，只算出现数量和概率
	 * 				2、输入多味中药时，计算出现概率的交集和并集
	 * @param batch
	 * @param medicines
	 * @return
	 */
	public static Map<String, String> statisMedicProbability(String batch,String medicines){
		if(medicines == ""){
			return null;
		}
		
		Map<String, String> resultMap = new HashMap<String, String>();
		// 1. 拆分中药名称
		
		String[] names = medicines.split(" ");
		if(names == null || names.length == 0){
			return null;
		}
		// 2、获取全部病历数据
		List<EHealthRecord> allRecrods = CWRelationMapping.queryEhealthData();
		
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
	 *  统计
	 * @param cmd
	 * @return
	 */
	public static Map<String, Integer> statisMedicineWithCMDescription(CMDescriptionClassify cmd){
		if(cmd == null || cmd.geteHealthRecords() == null || cmd.geteHealthRecords().size() == 0){
			return null;
		}
		
		Map<String, Integer> cnmedicineMap = new HashMap<String, Integer>(); // 中药处方统计
		Set<String> medicineSet = new TreeSet<String>();
		// 属于该类型的病历
		List<EHealthRecord> ehealthList = cmd.geteHealthRecords();
		for(EHealthRecord e : ehealthList){
			if(e.getChineseMedicines() != null && e.getChineseMedicines().size() > 0 ){
				for(ChineseMedicine c : e.getChineseMedicines()){
					if(medicineSet.add(c.getNameString())){
						// 新的中药
						cnmedicineMap.put(c.getNameString(), 1);
					}else{
						// set 已经有了中药
						int count = (Integer)cnmedicineMap.get(c.getNameString());
						count++;
						cnmedicineMap.remove(c.getNameString());
						cnmedicineMap.put(c.getNameString(), count);
					}
				}
			}
		}
		return cnmedicineMap;
	}
	
	
	/**
	 *  根据描述，匹配类型
	 * @param desc
	 * @param cmdList
	 * @return
	 */
	public static CMDescriptionClassify matchDescriptionClassify(String desc,List<CMDescriptionClassify> cmdList){
		if(desc == "" || cmdList == null || cmdList.size() == 0){
			return null;
		}
		// 匹配
		int maxcount = 0; // 最大匹配长度		
		int index = -1;    // cmdList 索引		
		int length = cmdList.size();
		
		for(int i = 0; i < length; i++ ){
			
			int count = 0;    // 匹配长度			
			String[] keywords = cmdList.get(i).getKeywords(); // 关键字
			
			for(String s : keywords ){
				if(desc.matches(".*" + s + ".*")){
					count++;
				}				
			}
			if(count > maxcount){
				maxcount = count;
				index = i;
			}
		}
		if(index != -1){
			return cmdList.get(index);
		}
		
		return null;
	}
	
	/**
	 *  根据病症判断病症类型
	 *  1、根据输入病症描述，与数据库中的病历数据进行文本相似度计算；
	 * 	2、 确定最为相似的病历；
	 * 	3、确定该病历的诊断类型；
	 * 	4、返回诊断类型；
	 * @param diag：诊断
	 * @param diagnosticsClassifies：诊断分类
	 * @return
	 */
	public static DiagnosticsClassify matchDiagnosticsClassify(String diag, List<DiagnosticsClassify> diagnosticsClassifies){
		if (diag == "" || diag == null || diagnosticsClassifies == null || diagnosticsClassifies.size() == 0) {
			return null;
		}
		DiagnosticsClassify result = null;
		
		DiagnosticsClassify tmp;
		int length = diagnosticsClassifies.size();
		int maxMatchNum = 0; // 关键字最大匹配数
		for(int i = 0; i < length; i++){
			tmp = diagnosticsClassifies.get(i);
			if(maxDiagMatchNum(diag,tmp) > maxMatchNum){
				result = tmp;
				maxMatchNum = maxDiagMatchNum(diag,tmp);
			}
		}
		return result;
	}
	
	/**
	 *  根据病症判断病症类型
	 *  1、根据输入病症描述，与数据库中的病历数据进行文本相似度计算；
	 * 	2、 确定最为相似的病历；
	 * 	3、确定该病历的诊断类型；
	 * 	4、返回诊断类型；
	 * @param diag：诊断
	 * @param diagnosticsClassifies：诊断分类
	 * @return
	 * @throws IOException 
	 */
	public static DiagnosticsClassify matchDiagnostics(String diag) throws IOException{
		if(diag == "" || diag == null){
			return null;
		}
		
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		// 1.   读取病历信息
		List<EHealthRecord> eHealthList = CWRelationMapping.queryEhealthData();
		// 2. 诊断类型构建
		List<DiagnosticsClassify> chineseDiagnostics = cwRelationMapping.createDiagnostics(DiagClassifyData.cnDiagClassify); // 中医诊断分类
		// 3. 中医诊断分类
		cwRelationMapping.chineseDiagnosticsClassify(eHealthList,chineseDiagnostics);//中医诊断分类
		// 4. 病症描述相似度计算
		double maxSimilarity = -1.0; //最大相似度
		String maxRegNo = ""; // 最大相似度病历的挂号号
		
		for(EHealthRecord eHealthRecord : eHealthList){
			if(eHealthRecord.getConditionsdescribed() != ""){
				double similarity = levenshtein(diag, eHealthRecord.getConditionsdescribed());
				if(similarity > maxSimilarity){
					maxSimilarity = similarity;
					maxRegNo = eHealthRecord.getRegistrationno();
				}
			}
		}
		
		// 5. 得到最相似的病历后，确定该病历的诊断类型
		
		DiagnosticsClassify resultClassify = null;
		
		if(maxRegNo != ""){
			for(DiagnosticsClassify d:chineseDiagnostics){
				if(d.geteHealthRecords() != null && d.geteHealthRecords().size() > 0){
					for(EHealthRecord e : d.geteHealthRecords()){
						if(e.getRegistrationno().equals(maxRegNo)){
							resultClassify = d;
						}
					}
				}
			}
		}
		return resultClassify;
	}
	
	/**
	 *  病症匹配
	 * @param diagString
	 * @param diagnosticsClassify
	 * @return 
	 */
	public static int maxDiagMatchNum(String diagString,DiagnosticsClassify diagnosticsClassify){
		
		if(diagString == "" || diagString == null || diagnosticsClassify == null){
			return 0;
		}
		
		String[] keywords = diagnosticsClassify.getKeywrods();
		int length = keywords.length;
		int count = 0; // 关键字匹配次数
		for(int i = 0; i < length; i++){
			if(diagString.matches(".*" + keywords[i] + ".*")){
				//
				count++;
			}
		}
		return count;
	}
	
	/**
	 *  统计病历list中的中药处方的数据
	 * @param eHealthRecords
	 * @return Map<中药名称，数量>
	 */
	public static Map<String, Integer> statisEhealthMedicine(List<EHealthRecord> eHealthRecords){
		if(eHealthRecords == null || eHealthRecords.size() == 0){
			return null;
		}
		//1、统计list中所有的中药名称
		List<String> allCnMedicines = new ArrayList<String>(); // 所有的中药名称（包含重复的相）
		for(EHealthRecord eRecord : eHealthRecords){
			if(eRecord.getChineseMedicines() != null && eRecord.getChineseMedicines().size() > 0){
				for(ChineseMedicine c : eRecord.getChineseMedicines()){
					allCnMedicines.add(c.getNameString());
				}
			}
		}
		
		//2、依次统计重复的名称
		Map<String, Integer> statisMedicines = MedicineStatics.staticsChineseMedicine(allCnMedicines);
		//3. 排序
//		statisMedicines = DiagMedicineProcess.sortMapByValue(statisMedicines);
		//4、返回结果
		return statisMedicines;
	}
	
	/**
	 *  对病历数据进行分类，并对类型进行编码
	 * @param keywords
	 * @param eHealthRecords
	 * @return
	 */
	public static Map<EHealthRecord, String> classifyEhealthMap(List<String> keywords,List<EHealthRecord> eHealthRecords){
		if(keywords == null || keywords.size() == 0 || eHealthRecords == null || eHealthRecords.size() == 0){
			return null;
		}
		Map<EHealthRecord, String> resultMap = new HashMap<EHealthRecord, String>();
		
		for(EHealthRecord e : eHealthRecords){
			String descString = "";
			String codeString = "";
			for(String k : keywords){
				// xx|k1
				String[] strings = k.split("\\|");
				if(e.getConditionsdescribed().matches(".*" + strings[0] + ".*")){
					descString += strings[0] + " ";
					codeString += strings[1] + " ";
				}
			}
			if(descString != "" && codeString != ""){
				resultMap.put(e, descString + "|" + codeString);
			}
		}
		
		return resultMap;
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
     * 　　DNA分析 　　拼字检查 　　语音辨识 　　抄袭侦测 
     *  
     * @createTime 2012-1-12 
     */  
    public static double levenshtein(String str1,String str2) {  
        //计算两个字符串的长度。  
        int len1 = str1.length();  
        int len2 = str2.length();  
        //建立上面说的数组，比字符长度大一个空间  
        int[][] dif = new int[len1 + 1][len2 + 1];  
        //赋初值，步骤B。  
        for (int a = 0; a <= len1; a++) {  
            dif[a][0] = a;  
        }  
        for (int a = 0; a <= len2; a++) {  
            dif[0][a] = a;  
        }  
        //计算两个字符是否一样，计算左上的值  
        int temp;  
        for (int i = 1; i <= len1; i++) {  
            for (int j = 1; j <= len2; j++) {  
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {  
                    temp = 0;  
                } else {  
                    temp = 1;  
                }  
                //取三个值中最小的  
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,  
                        dif[i - 1][j] + 1);  
            }  
        }  
//        System.out.println("字符串\""+str1+"\"与\""+str2+"\"的比较");  
        //取数组右下角的值，同样不同位置代表不同字符串的比较  
//        System.out.println("差异步骤："+dif[len1][len2]);  
        //计算相似度  
        float similarity =1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length()); 
        
        return similarity;  
    }  
  
    //得到最小值  
    private static int min(int... is) {  
        int min = Integer.MAX_VALUE;  
        for (int i : is) {  
            if (min > i) {  
                min = i;  
            }  
        }  
        return min;  
    }
    
    /**
     *  字符串组合
     * @param strings
     * @return
     */
    public static List<ArrayList<String>> getCombination(String[] strings){
    	if(strings == null || strings.length == 0){
    		return null;
    	}
    	
    	List<ArrayList<String>> combinationList = new ArrayList<ArrayList<String>>();
    	//获得字符的全部组合 －－－至少是两个
    	int length = strings.length; // 长度
    	
    	for(int i = 2; i <= length; i++){
    		//至少是两个
    		
    	}
    	
    	return combinationList;
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
     *  根据list 去掉map中的某些数据
     * @param maps
     * @param list
     * @return
     */
    public static Map<String, Integer> removeMapOfList(Map<String,Integer> maps,List<String> list){
    	if(maps == null || maps.isEmpty()){
    		return null;
    	}
    	if(list == null || list.isEmpty()){
    		return maps;
    	}
    	
    	for(String s : list){
    		maps.remove(s);
    	}
    	return maps;
    }
	
    /**
	 *  string to array （split with 空格）
	 * @param string
	 * @return
	 */
	public static String[]  stringToArray(String string){
		if(string == ""){
			return null;
		}
		if(string.split(" ") != null){
			return string.split(" ");
		}
		return null;
	}
	
	public static void main(String[] args){
		
//		CWRelationMapping cwRelationMapping = new CWRelationMapping();
//		List<EHealthRecord> eList = cwRelationMapping.queryEhealthData();
//		
//		Map<String, HashMap<String, String>> resultMap = getConditionDescription(eList.get(1));
//		System.out.println(eList.get(1).getConditionsdescribed());
//		System.out.println(resultMap);
//		String s1 = "abcdefg";
//		String[] ss1 = {"a","g","d"};
//		String[] ss2 = {"a","j"};
//		System.out.println(isMaxMatch(s1, ss1));
//		System.out.println(isMaxMatch(s1, ss2));
		
//		String string = "纳眠可，便秘";
//		Map<String, String> result = getDescritionCode(string);
		
	}
}
