package com.um.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.um.data.EHRRule;

public class BasedOnRulePredict {
	
	
	/**
	 *  Predict medicines based on rules.
	 * @param description
	 * @return
	 */
	public static List<String> predictBasedOnRules(String description){
		if ( description.equals("") ) {
			return null;
		}
		List<String> medicineList = new ArrayList<String>();
		
		// All Rules.
		Map<String, ArrayList<String>> rules = EHRRule.getEhrRules();
		
		Set<String> medicineSet = new HashSet<String>(); // All medicines without repeat
		
		Set<String> ruleKeySet = rules.keySet();
		
		for (String key : ruleKeySet) {
			if (description.matches(".*" + key + ".*")) {
				medicineSet.addAll(rules.get(key));
			}
		}
		
		// Set to list
		medicineList.addAll(medicineSet);
		
		return medicineList;
	}
}
