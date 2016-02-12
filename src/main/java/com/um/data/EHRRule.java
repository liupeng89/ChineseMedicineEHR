package com.um.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EHRRule {
	
	public static String[] ehrRuleStrings = {
		
		"术后:黄芪,党参",
		
		"单纯中医药治疗:石见穿,黄芪,白蛇花草,炒薏仁,龙葵",
		
		"化疗后:莪术,山慈菇,红豆杉",
		
		"气虚:太子参,白朮,黄芪,甘草",
		
		"贫血:当归,黄精,鹿角霜",
		
		"胸痛:延胡索,牛蒡子,木蝴蝶",
		
		"腹泻:石榴皮,五味子,补骨脂,怀山药",
		
		"倦怠乏力:西洋参",
		
		"厌食:炒谷芽,山楂",
		
		"食欲减退:炒谷芽,山楂",
		
		"纳差:炒谷芽,山楂",
		
		"阴虚:沙参,麦冬",
		
		"胸水:猪苓,葶苈子",
		
		"恶心:苏梗,丁香,姜制砂仁",
		
		"腹胀:厚朴,枳壳,生大黄",
		
		"便秘:厚朴,枳壳,生大黄",
		
		"气滞血瘀疼痛:莪术,延胡索",
		
		"失眠:酸枣仁,磁石",
		
		"口渴多饮:石斛,天仁粉",
		
		"纳差:炒麦芽",
		
		"红血痰:紫珠草,三七"
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
