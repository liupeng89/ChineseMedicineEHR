package com.um.util;

import java.util.ArrayList;
import java.util.List;

public class EhealthUtil {
	
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
