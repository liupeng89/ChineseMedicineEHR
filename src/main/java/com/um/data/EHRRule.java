package com.um.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EHRRule {
	
	public static String[] ehrRuleStrings = {
		
		"术后:黄芪,党参",
		
		"单纯中医药治疗:石见穿,黄芪",
		
		"纳差:炒麦芽",
		
		"红血痰:紫珠草"
	};
	
	/**
	 * Convert the rule to map
	 * @return
	 */
	public static Map<String, ArrayList<String>> getEhrRules(){
		
		Map<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
		
		for (String ruleString : ehrRuleStrings) {
			String[] tmp = ruleString.split(":");
			if (tmp.length != 2) {
				continue;
			}
			// 0: rule , 1: contents
			String[] contents = tmp[1].split(",");
			ArrayList<String> medicineList = new ArrayList<String>();
			for (String c : contents) {
				medicineList.add(c);
			}
			result.put(tmp[0], medicineList);
		}
		
		return result;
	}
}
