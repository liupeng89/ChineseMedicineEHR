package com.um.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import com.um.classify.CWRelationMapping;
import com.um.data.DiagClassifyData;
import com.um.model.EHealthRecord;

public class MedicineByDescription {
	
	/**
	 * 根据描述，生成处方
	 * @param description
	 * @return
	 */
	public static List<String> getMedicineByDesc(String description){
		if(description == ""){
			return null;
		}
		List<String> medicieList = new ArrayList<String>(); // 中药处方结果
		
		int threshold = 15; // 中药处方阈值：预计输出15味中药
		int currentCount = 0; // 当前的中药处方数量

		/**
		 *  1. 统计所有的中药处方，并排序
		 */
		// 1.2、读取病例数据
		List<EHealthRecord> allEHealthRecords = CWRelationMapping.queryEhealthData();
		
		// 所有的中药处方统计 <名称，数量>
		Map<String, Integer> allMedicineMap = DiagMedicineProcess.statisEhealthMedicine(allEHealthRecords);
		allMedicineMap = DiagMedicineProcess.sortMapByValue(allMedicineMap); // 统计结果排序
		/**
		 *  2. 找出出现概率大于90%的，作为结果 
		 */
		int allRecordLength = allEHealthRecords.size(); // 全部病历的数量
		double percent = 0.9;
		List<String> medicineWithNinePercent = DiagMedicineProcess.statisMedicineWithPercent(allMedicineMap, allRecordLength, percent);
		medicieList.addAll(medicineWithNinePercent);
		
		// 去掉出现概率较大的
		allMedicineMap = DiagMedicineProcess.removeMapInList(allMedicineMap, medicineWithNinePercent);
		
		/**
		 * 	3. 累计后续中药，判断累加结果是否大于90%，根据判断规则输出
		 * 		判断条件：
		 * 				1）出现概率的并集大于90 ％
		 * 				2）需要结合对病症描述的分析一起判断
		 */
		
		// 3.1 基于病症描述，对病例进行统计，输出统计中药处方
		// 按照关键字的并集
		Set<String> cnmedicienSet = DiagMedicineProcess.getMedicinesByDescriptionMix(description, allEHealthRecords);
		if(cnmedicienSet == null || cnmedicienSet.isEmpty()){
			return null;
		}
		Set<String> cnmedicineSet = DiagMedicineProcess.getMedicinesByDescriptionUnion(description, allEHealthRecords);
		
		// 3.2 中药分组
		double unionpercent = 0.9; // 并集的出现概率----0.9
		currentCount = medicieList.size(); // 当前筛选出的中药处方数量
		while(currentCount <= threshold){
			// 中药处方数量不足threshold
			// 1. 累计计算，得出一组中药(判断这组中药的并集的出现概率 大于90%)
			List<String> accumulateList = DiagMedicineProcess.accumulateMedicines(allMedicineMap, allEHealthRecords, unionpercent); // 统计累计出现概率之和大于percent的多为中药
			if(accumulateList == null || accumulateList.isEmpty()){
				break; // 剩下的中药不足以构成分组
			}
			
			Map<String, Integer> calculMap = new LinkedHashMap<String,Integer>(accumulateList.size()); // 改组中药的中药名称以及数量
			for(String s : accumulateList){
				calculMap.put(s, allMedicineMap.get(s)); // 暂存中药与出现次数 map
			}
			// 去掉已经选择的中药
			allMedicineMap = DiagMedicineProcess.removeMapInList(allMedicineMap, accumulateList); 
			// 2. 计算这些中药之间的相关性，并根据规则进行分析－－－对这些中药进行组合，确定组合的交集是否大于 50%，大于，则同时出现的概率很大－－暂定两两组合
			List<String> combitationList = getMedicineByGroupRelation(accumulateList,allEHealthRecords);
			accumulateList.addAll(combitationList); // 
			// 3. 根据中医描述，对病例进行统计，
			
			// 4. 确定需要输出的中药，取与描述所统计的中医组合取并集
			Set<String> unionSet = new HashSet<String>(); // 取两者之间的并集
			
			for(String s : accumulateList){
				if(cnmedicineSet.contains(s) && cnmedicineSet != null){
					unionSet.add(s); // 
				}
			}
			
			medicieList.addAll(unionSet);
			currentCount = medicieList.size();
		}
		
		/**
		 * 	4. 整理结果，并输出最终结果
		 */
		return medicieList;
	}
	
	/**
	 * 	Predict medicines based on the diagnose and description info
	 * 
	 * @param batch
	 * @param diagnose
	 * @param description
	 * @return medicines
	 */
	public static List<String> getMedicineByDiagAndDesc(String batch,String diagnose,String description){
		if("".equals(diagnose) || "".equals(description)) return null;
		
		List<String> medicineList = new ArrayList<String>(); // predict medicines result
		
		int outputnumber = 15; // the number of output

		/**
		 * 1. statistics all records to choice the percent of medicines large than 90% as predict result
		 */
		// 1.1 get all records with same batch
		List<EHealthRecord> eHealthRecordsByBatch = getRecordsByBatch(batch); // all record with same batch
		
		// 1.3 statistics name and number of medicines in this batch records
		Map<String, Integer> allMedicineMap = DiagMedicineProcess.statisEhealthMedicine(eHealthRecordsByBatch);
		
		// 1.4  find the medicines with percent large than 90% 
		int allRecordsNum = eHealthRecordsByBatch.size(); // the number of this batch records
		double percent = 0.9; // the percent 
		
		List<String> medicineWithInevitable = DiagMedicineProcess.statisMedicineWithPercent(allMedicineMap, allRecordsNum, percent);
		if(medicineWithInevitable != null && medicineWithInevitable.size() > 0){
			medicineList.addAll(medicineWithInevitable); //the medicine with percent large than 90%
		}
		
		// 1.5 remove the chock medicine to avoid repeating 
		allMedicineMap = DiagMedicineProcess.removeMapInList(allMedicineMap, medicineWithInevitable);
		
		// 1.6 return with enough numbers of medicines
		if(medicineList.size() > outputnumber) return medicineList;
		
		/**
		 * 2. statistics based on the diagnose and description info to get medicines
		 */
		// 2.1 split the diagnose
		String[] diagkeywords = diagnose.split(" ");
		if( diagkeywords.length == 0 ){
			return medicineList; // return with no diagnose
		}
		
		// 2.2 classify the records based on the diagnose info
		List<EHealthRecord> classifiedRecords = DiagMedicineProcess.getRecordsByDiagnose(diagkeywords, eHealthRecordsByBatch);
		// 2.3 statistics medicines based on the description info
		Set<String> cnmedicineSet = DiagMedicineProcess.getMedicinesByDescription(description, classifiedRecords);
		
		// 2.4 add the statistics result to the final result
		if(cnmedicineSet != null && cnmedicineSet.size() > 0){
			for(String s : cnmedicineSet){
				if(!medicineList.contains(s)){
					medicineList.add(s);
				}
			}
		}
		// return with enough number of medicines
		if(medicineList.size() > outputnumber){
			return medicineList.subList(0, 15);
		}
		
		/**
		 * 3. other statistics and analysis methods when no enough numbers of medicines
		 */
		return medicineList;
	}
	
	/**
	 * 根据诊断和中医病症描述－－－预测处方
	 * @param batch
	 * @param diagnose
	 * @param description
	 * @return 预测处方
	 */
	public static List<String> getMedicineByDiagAndDesc(String diagnose,String description){
		if(diagnose == "" || description == ""){
			return null;
		}
		List<String> medicineList = new ArrayList<String>(); // 中医list
		
		int threshold = 15; // 中药处方阈值：预计输出15味中药

		/**
		 * 1. 对中医处方进行统计，选择出现概率大于90%的中药作为结果输出；
		 */
		// 1.1 读取数据库种病例数据
		List<EHealthRecord> eHealthRecordsByBatch = CWRelationMapping.queryEhealthData();
		
		// 1.2 选取批次
		
		// 1.3 统计所有的中药处方统计－－－－ <名称，数量>
		Map<String, Integer> allMedicineMap = DiagMedicineProcess.statisEhealthMedicine(eHealthRecordsByBatch);
		
		// 1.4  找出出现概率大于90%的，作为结果 
		int allRecordLength = eHealthRecordsByBatch.size(); // 本批次病历的数量
		double percent = 0.9; // 中药出现概率
		
		List<String> medicineWithNinePercent = DiagMedicineProcess.statisMedicineWithPercent(allMedicineMap, allRecordLength, percent);
		if(medicineWithNinePercent != null && medicineWithNinePercent.size() > 0){
			medicineList.addAll(medicineWithNinePercent); //出现概率大于90%的中药名称
		}
		
		// 1.5 去掉出现概率较大的，后续分析
		allMedicineMap = DiagMedicineProcess.removeMapInList(allMedicineMap, medicineWithNinePercent);
		
		// 1.6 若满足数量，则直接输出
		if(medicineList.size() > threshold){
			return medicineList;
		}
		
		/**
		 * 2. 对中药进行按照并集进行分组，在根据各个组内的相关性和诊断＋描述，进行分析
		 */
		// 2.1 根据中医诊断，对病例进行分类，取出符合该诊断类型的病例（假设用户输入－－空格分割：肺癌 气虚 互结）
		String[] diagkeywords = diagnose.split(" ");
		if( diagkeywords.length == 0 ){
			return medicineList; // 返回已经查到的中药 
		}
		
		// 2.2 根据诊断，对病例数据进行分类
		List<EHealthRecord> classifiedRecords = DiagMedicineProcess.getRecordsByDiagnose(diagkeywords, eHealthRecordsByBatch);
		
		// 2.3 分析用户输入描述，并提取关键字
		Set<String> cnmedicineSet = DiagMedicineProcess.getMedicinesByDesc(description, classifiedRecords);
		
		// 2.4 对关键字按照or关系，分别统计该关键字下的中药处方（去除必然出现的中药）
		if(cnmedicineSet != null && cnmedicineSet.size() > 0){
			for(String s : cnmedicineSet){
				if(!medicineList.contains(s)){
					medicineList.add(s);
				}
			}
		}
		
		
		if(medicineList.size() > threshold){
			return medicineList.subList(0, 15);
		}
		
		/**
		 * 3. 若统计的中药还是不足，在根据起一些人工规则，继续推导
		 */
		
		return medicineList;
	}
	
	/**
	 *  Get the similar EHR records based the batch, diagnose and description info
	 * @param batch
	 * @param diagnosehwo
	 * @param description
	 * @return
	 */
	public static List<EHealthRecord> getSimilaryEHealthRecords(String batch,String diagnose,String description){
		if("".equals(batch)||"".equals(diagnose)||"".equals(description)) return null;
		
		// 1.1 get all records of this batch
		List<EHealthRecord> eHealthRecordsByBatch = getRecordsByBatch(batch);
		
		// 1.2 split the diagnose
		String[] diagkeywords = diagnose.split(" ");
		if(diagkeywords.length == 0 || diagkeywords == null){
			return null; 
		}
		// 1.3 classify the records based on diagnose info
		List<EHealthRecord> classifiedRecords = DiagMedicineProcess.getRecordsByDiagnose(diagkeywords, eHealthRecordsByBatch);
		
		// 1.4 get the similar records based on the description info
		List<EHealthRecord> similarRecords = DiagMedicineProcess.getEhealthRecordByDescription(description, classifiedRecords);
		
		// 1.5 remove the repeat records
		Set<EHealthRecord> eSet = new HashSet<EHealthRecord>();
		
		if( similarRecords != null && similarRecords.size() > 0 ){
			for( EHealthRecord e : similarRecords ){
				eSet.add(e);
			}
		}
		// 1.6 return the similar records
		List<EHealthRecord> result = new ArrayList<EHealthRecord>();
		result.addAll(eSet);
		return result;
	}
	
	
	/**
	 *  根据分组关系，确定中药处方
	 *  	主要实现：1、根据分组，确定该分组内的全部中药；
	 *  			2、对这些中药两两组合，确定组合的交集是否很大：大于60%
	 *  			3、若成立，则说明这两味中药同时出现的概率很大；
	 *  			4、若不成立，则说明同时出现的概率较小；
	 * @param list
	 * @param eRecords
	 * @return
	 */
	public static List<String> getMedicineByGroupRelation(List<String> list,List<EHealthRecord> eRecords){
		if(list == null || list.size() == 0 || eRecords == null || eRecords.size() == 0){
			return null;
		}
		List<String> resultList = new ArrayList<String>();
		double percent = 0.5; // 交集概率上限
		int length = eRecords.size(); // 总病例数
		// 1. 确定中药名称两两组合
		List<String> combinationList = EhealthUtil.getCombination(list); //中药名称组合
		
		// 2. 确定所有组合的出现概率的交集是否大于60% ，若是则输出；
		if(combinationList == null ){
			return null;
		}
		String[] combinations = null; // 组合分解
		Set<String> medicineSet = new HashSet<String>(); //避免重复
		for(String s : combinationList){
			// 1. 组合解析－－－－A|B
			combinations = s.split("\\|");// 组合解析
			// 2. 计算交集
			int mix = EhealthRecordMath.getMix(combinations, eRecords); // 交集
			// 3. 判断
			if(mix > length * percent){
				// 交集的概率很大，即同时出现的概率较大
				medicineSet.add(combinations[0]);
				medicineSet.add(combinations[1]);
			}
		}
		resultList.addAll(medicineSet);
		return resultList;
	}
	
	/**
	 * Format the request parameters
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> parseRequestParameter(HttpServletRequest request){
		if(request == null) return null;
		Map<String, String> resultMap = new HashMap<String, String>();
		// 1. parse the request parameters
		String batch = request.getParameter("batch").trim(); // batch 
		String threshold = request.getParameter("threshold").trim(); // threshold of machine learning
		
		// Time status
		String timeStatusString = request.getParameter("timestatus").trim();
//		System.out.println(timeStatusString);
//		String timeStatus = "";
//		if (timeStatusString.equals("cmtreat")) {
//			timeStatus = "单纯中医药治疗";
//		}
//		if (timeStatusString.equals("shuqian")){timeStatus = "术前";}
//		if (timeStatusString.equals("shuhou")){timeStatus = "术后";}
//		if (timeStatusString.equals("zhiliaozhong")){timeStatus = "放疗中";}
//		if (timeStatusString.equals("zhiliaohou")){timeStatus = "放疗后";}
//		if (timeStatusString.equals("hualiaozhong")){timeStatus = "化疗中";}
//		if (timeStatusString.equals("hualiaohou")){timeStatus = "化疗后";}
//		if (timeStatusString.equals("fenzi")){timeStatus = "分子靶向药物";}
//		if (timeStatusString.equals("mianyi")){timeStatus = "免疫治疗";}
		// 2. diagnose 
		String diagnoseString = "";
		String xuString = request.getParameter("xu").trim();
		String tanyuString = request.getParameter("tanyu").trim();
		String tanshiString = request.getParameter("tanshi").trim();
		String[] zhengxingString = request.getParameterValues("zhengxing");
		
		diagnoseString += xuString;
		diagnoseString += tanyuString.equals("yes") ? " 痰瘀" : " ";
		diagnoseString += tanshiString.equals("yes") ? " 痰湿" : " ";
		
		if(zhengxingString != null && zhengxingString.length > 0){
			for(String s : zhengxingString){
				diagnoseString += s + " ";
			}
		}
		
		// 3. description
		// 3.1 parse the description parameters
		String descriptionString = ""; // description
		String hanre = request.getParameter("hanre"); // 寒热
		String sweat = request.getParameter("sweat"); // 汗
		String xonglei = request.getParameter("xonglei"); // 胸肋痛
		String futong = request.getParameter("futong"); // 腹痛
		String[] tengtong = request.getParameterValues("tengtong"); //疼痛
		String[] bodydiscomfort = request.getParameterValues("bodydiscomfort"); //头身胸腹不适
		String defecate = request.getParameter("defecate"); //大便
		String[] constipation = request.getParameterValues("constipation"); //便秘
		String urinate = request.getParameter("urinate"); // 小便
		String tonguecolor = request.getParameter("tonguecolor"); // 舌色
		String coatedtongue = request.getParameter("coatedtongue"); // 舌苔
		String sputumamount = request.getParameter("sputumamount"); // 痰量
		String sputumcolor = request.getParameter("sputumcolor"); // 痰色
		String sleep = request.getParameter("sleep"); // 眠
		String na = request.getParameter("na"); // 纳
		String energy = request.getParameter("energy"); // 气力
		String[] pulse = request.getParameterValues("pulse"); // 脉
		String thirst = request.getParameter("thirst"); //口渴
		String taste = request.getParameter("taste"); // 口味
		String cough = request.getParameter("cough"); // 咳嗽
		
		// 3.2 format the description for return
		descriptionString = timeStatusString + "," + hanre + "," + sweat + "," + xonglei + "," + futong + ","
							+ convertArrayToString(tengtong) + convertArrayToString(bodydiscomfort)
							+ defecate + "," + convertArrayToString(constipation) + urinate + "," 
							+  tonguecolor + "," + coatedtongue + "," + sputumamount
							+ "," + sputumcolor + "," + sleep + "," + na + "," + energy + "," 
							+ convertArrayToString(pulse) + thirst
							+ "," + taste + "," + cough;
		
		resultMap.put("batch", batch); 
		resultMap.put("diagnose", diagnoseString);
		resultMap.put("description", descriptionString);
		resultMap.put("threshold", threshold);
//		resultMap.put("timeStatus", timeStatus);
		return resultMap;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String getDescriptionString(HttpServletRequest request){
		if (request == null) {
			return "";
		}
		String description = "";
		
		String hanre = request.getParameter("hanre"); // 寒热
		String sweat = request.getParameter("sweat"); // 汗
		String xonglei = request.getParameter("xonglei"); // 胸肋痛
		String futong = request.getParameter("futong"); // 腹痛
		String[] tengtong = request.getParameterValues("tengtong"); //疼痛
		String[] bodydiscomfort = request.getParameterValues("bodydiscomfort"); //头身胸腹不适
		String defecate = request.getParameter("defecate"); //大便
		String[] constipation = request.getParameterValues("constipation"); //便秘
		String urinate = request.getParameter("urinate"); // 小便
		String tonguecolor = request.getParameter("tonguecolor"); // 舌色
		String coatedtongue = request.getParameter("coatedtongue"); // 舌苔
		String sputumamount = request.getParameter("sputumamount"); // 痰量
		String sputumcolor = request.getParameter("sputumcolor"); // 痰色
		String sleep = request.getParameter("sleep"); // 眠
		String na = request.getParameter("na"); // 纳
		String energy = request.getParameter("energy"); // 气力
		String[] pulse = request.getParameterValues("pulse"); // 脉
		String thirst = request.getParameter("thirst"); //口渴
		String taste = request.getParameter("taste"); // 口味
		String cough = request.getParameter("cough"); // 咳嗽
		
		description = hanre + "," + sweat + "," + xonglei + "," + futong + ","
				+ convertArrayToString(tengtong) + convertArrayToString(bodydiscomfort)
				+ defecate + "," + convertArrayToString(constipation) + urinate + "," 
				+  tonguecolor + "," + coatedtongue + "," + sputumamount
				+ "," + sputumcolor + "," + sleep + "," + na + "," + energy + "," 
				+ convertArrayToString(pulse) + thirst
				+ "," + taste + "," + cough;
		
		return description;
	}
	
	
	/**
	 * 	Format the description with standard Chinese description
	 * @param desString
	 * @return
	 */
	public static String getFormatedDescirption(String desString){
		if("".equals(desString)) return "";
		
		String result = "";
		
		// 1. get the standard description of Chinese
		Map<String, String> descTableMap = convertArraysToMap(DiagClassifyData.descriptionStrings);
		Map<String, String> normalTableMap = convertArraysToMap(DiagClassifyData.normalAndBaddescription);
		
		// 2. format the description of input to return 
		String[] splits = desString.split(",");
		if( splits == null || splits.length == 0 ) return result;
		
		int length = splits.length;
		for (int i = 0; i < length; i++) {
			String s = splits[i];
			if(normalTableMap.get(s) == null) continue;
			if( !normalTableMap.get(s).equals("0") ){ 
				result += (i== length-1) ? descTableMap.get(s) : descTableMap.get(s) + ",";
			}
		}
		return result;
	}
	
	/**
	 *  convert the array of string to a string.
	 * @param arrays
	 * @return
	 */
	public static String convertArrayToString(String[] arrays){
		if(arrays == null || arrays.length == 0){
			return "";
		}
		String resultString = "";
		for( String s : arrays ){
			resultString += s + ",";
		}
		return resultString;
	}
	
	/**
	 *  Convert array of description to map
	 * @param arrays
	 * @return
	 */
	public static Map<String, String> convertArraysToMap(String[] arrays){
		if( arrays == null || arrays.length == 0 ) return null;

		Map<String, String> result = new HashMap<String, String>();
		for( String a : arrays ){
			String[] split = a.split(":");
			if( split == null || split.length != 2){
				continue;
			}
			result.put(split[0], split[1]);
		}
		return result;
	}
	
	/**
	 * Convert array of description keywords to map list
	 * @param arrays
	 * @return
	 */
	public static Map<String, ArrayList<String>> convertArraysToMapList(String[] arrays){
		if (arrays == null || arrays.length == 0) return null;
		
		Map<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
		
		for (String string : arrays) {
			ArrayList<String> contents = new ArrayList<String>();
			String[] splits = string.split(":");
			if (splits == null || splits.length != 2) continue;
					
			String[] list = splits[1].split("\\|");
			if (list == null || list.length == 0) continue;
			for (String lString : list) {
				contents.add(lString);
			}
			result.put(splits[0], contents);
		}
		
		return result;
	}
	
	/**
	 *  Get the e-health records by the batch
	 *  
	 * @param batch
	 * @return
	 */
	public static List<EHealthRecord> getRecordsByBatch(String batch){
		
		if( batch.equals("") ) return null;
		// built the conditions structure of batch
		Document conditons = new Document();
		if(!batch.equals("")){
			// get the four char
			conditons.append("ehealthrecord.batch", batch.substring(0, 4));
		}
		// 1.2 选取批次
		List<EHealthRecord> eHealthRecordsByBatch = EhealthUtil.getEhealthRecordListByConditions(conditons);
		return eHealthRecordsByBatch;
	}
	
	/**
	 *  格式化病症描述
	 *  	：将描述文本转化成输入格式的描述方式
	 * @param description
	 * @return
	 */
	public static String formattedDescriptionByCount(String description){
		
		if( description.equals("") ) return null;
		String formattedDescriptionString = "";
		
		// keyword list 
		Map<String, ArrayList<String>> keywordList = MedicineByDescription.convertArraysToMapList(DiagClassifyData.descKeywords);
		// the description code
		Set<String> descriptionFormatted = new HashSet<String>();
		Set<String> keywordListSet = keywordList.keySet();
		for (String k : keywordListSet) {
			ArrayList<String> contents = keywordList.get(k);
			if (contents == null || contents.size() == 0 ) { continue; }
			
			for (String c : contents) {
				if (description.contains(c)) {
					// match 
					descriptionFormatted.add(k);
					break;
				}
			}
		}
		
		if (descriptionFormatted.size() == 0) {
			return formattedDescriptionString;
		}
		// key word reference
		Map<String, String> descTableMap = MedicineByDescription.convertArraysToMap(DiagClassifyData.descriptionStrings);
		Map<String, String> normalTableMap = MedicineByDescription.convertArraysToMap(DiagClassifyData.normalAndBaddescription);
		
		for (String d : descriptionFormatted) {
			if (normalTableMap.get(d) == null || normalTableMap.get(d).equals("0") || descTableMap.get(d) == null) {
				continue;
			}
			formattedDescriptionString += descTableMap.get(d) + ",";
		}
		
		return formattedDescriptionString;
	}
	
}
