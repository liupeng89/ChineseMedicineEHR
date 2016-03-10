package com.um.mongodb.converter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
/**
 *
 * @author lp
 */
public class MedicineStatics {
    
    /**
     *  统计list 中药名称和出现次数
     * @param list
     * @return
     */
    public static HashMap<String, Integer> staticsChineseMedicine(List<String> list){
    	
    	if(list == null || list.size() == 0) return null;
    	
    	HashMap<String, Integer> results = new HashMap<String, Integer>();
    	
    	//Add the first one medicine
    	results.put(list.get(0), 1);
    	
    	for(int i = 1; i < list.size(); i++){
    		results = statics(list.get(i).trim(), results);
    	}
        
        results = sortMapByValue(results); //Sorted the statistics result
        
    	return results;
    } 
    
    /**
     * 统计list 中重复中药的数量
     * @param string
     * @param tample
     * @return
     */
    public static HashMap<String, Integer> statics(String string,HashMap<String, Integer> tample){
    	
    	boolean flag = false; //判断是否已经统计
    	
    	for(int i = 0; i < tample.size() ; i++){
    		    		
    		if(tample.get(string) != null){
    			// 在里面
    			int count = tample.get(string);
    			count++; // 数量＋1
    			tample.remove(string); // 更新数据
    			tample.put(string, count);
    			flag = true;
                break;
    		}    		
    	}
    	if(!flag){
    		//不在
    		tample.put(string, 1);
    	}
    	return tample;
    }
    
    /**
     *  中药名称组合
     * @param strs
     * @return
     */
    public static List<String> combiantion(String[] strs){  
	    if(strs.length == 0) return null;  
	   
	    List<String> list = new ArrayList<String>();
	    
	    Stack<String> stack = new Stack<String>();  
	    
	    for(int i = 2; i <= strs.length; i++){  
	        combine(strs, 0, i, stack, list);  
	    }
	    
	    List<String> resultList = new ArrayList<String>();
		  
		for(String s : list){
			 String[] ss = s.split(",");
			 String beginString = ss[0].replace("[", "");
			 String endString = ss[ss.length-1].replace("]", "");
			 ss[0] = beginString;
			 ss[ss.length-1] = endString;
			  
			 String nameString = "";
			 for(int i = 0; i < ss.length; i++){
				 nameString += ss[i] + "|";
			 }
			 resultList.add(nameString);
		 }
		 return resultList;
	}  
    
    
    /**
     * 从字符数组中第begin个字符开始挑选number个字符加入list中
     * @param strs
     * @param begin
     * @param number
     * @param stack
     * @param list
     */
	public static void combine(String[] strs, int begin, int number, Stack<String> stack,List<String> list){  
      if(number == 0){  
       list.add(stack.toString());
       return ;  
      }  
      if(begin == strs.length){  
       return;  
      }  
       stack.push(strs[begin]);  
       combine(strs, begin + 1, number - 1, stack,list);  
	   stack.pop();  
	   combine(strs, begin + 1, number, stack,list);  
	}  
    
   
    /**
     *  按值对map进行排序
     * @param orimap
     * @return
     */
    public static HashMap<String, Integer> sortMapByValue(HashMap<String, Integer> orimap){
    	if(orimap == null || orimap.isEmpty()){
    		return null;
    	}
    	
    	HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
    	
    	List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String,Integer>>(orimap.entrySet());
    	
    	Collections.sort(entryList, new Comparator<Map.Entry<String,Integer>>(){

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
}
