package com.um.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.um.data.DiagClassifyData;
import com.um.util.DiagMedicineProcess;
import com.um.util.MedicineByDescription;

public class Test01 {
	
	/**
	 * 
	 * @param description
	 * @return
	 */
	public static String formattedDescriptionByCount(String description){
		if( description.equals("") ){
			return null;
		}
		String formattedDescriptionString = "";
		//
		Map<String, HashMap<String, ArrayList<String>>> keyworCodeMap = DiagMedicineProcess.createReference(DiagClassifyData.descriptionKeywords);
		
		// 3. 根据输入，确定输入编码
		Map<String, String> formattedMap = new HashMap<String, String>();
		Set<String> projectKeySet = keyworCodeMap.keySet();
		// project
		for( String project : projectKeySet ){
			// status
			Set<String> statusSet = keyworCodeMap.get(project).keySet();
			if( statusSet == null || statusSet.size() == 0 ){
				continue;
			}
			
			for( String status : statusSet ){
				int index = 0;
				ArrayList<String> keyArrayList = keyworCodeMap.get(project).get(status);
				if( keyArrayList == null || keyArrayList.size() == 0 ){
					continue;
				}
				
				for( String k : keyArrayList ){
					if( description.matches(".*" + k + ".*")){
						formattedMap.put(status, "1");
						break;
					}
					if(index == statusSet.size() - 1){
						formattedMap.put(status, "0");
					}
					index++;
				}
			}
		}
		// Tanslation
		Map<String, String> descTableMap = MedicineByDescription.convertArraysToMap(DiagClassifyData.descriptionStrings);
		Map<String, String> normalTableMap = MedicineByDescription.convertArraysToMap(DiagClassifyData.normalAndBaddescription);
		
		Set<String> formattedSet = formattedMap.keySet();
		if( formattedSet == null || formattedSet.size() == 0 ){
			return "";
		}
		for( String f : formattedSet ){
			if( normalTableMap.get(f).equals("0") || formattedMap.get(f).equals("0")){
				continue;
			}
			formattedDescriptionString += descTableMap.get(f) + ",";
		}
		return formattedDescriptionString;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String desc = "20111112于顺德第一人民医院纤支镜：左肺癌，病理：中分化腺癌。20111110陆总PET-CT：右肺上叶周围型癌并双肺弥漫转移，纵隔及左肺门多发淋巴结转移，右侧乳腺小结节，不除外恶性病变。20111221开始口服埃克替尼。现右乳右下象限可扪及1.5×1.5cm肿块，稍有疼痛，咳嗽，纳眠可，二便调舌质淡，舌苔黄微腻，脉弦细";
//		String formatted = formattedDescriptionByCount(desc);
//		System.out.println(formatted);
		String diag = "肺癌气虚痰瘀互结";
		
		String diagnose = "";
		String[] diagKeywords = DiagClassifyData.diagKeywords;
		for( String k : diagKeywords ){
			if(diag.matches(".*" + k + ".*")){
				diagnose += k + " ";
			}
		}
		System.out.println(diagnose);
	}

}
