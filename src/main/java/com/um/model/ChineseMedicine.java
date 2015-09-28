package com.um.model;

import java.util.List;

public class ChineseMedicine {
	
	/**
	 *  中药处方类
	 *    主要包括：中药名字，别名，数量，单位
	 * @param args
	 */
	
	/**
	 * 中药名字
	 */
	private String nameString;
	/**
	 * 别名
	 */
	private List<String> biasList;	
	/**
	 * 数量
	 */
	private String numberString;
	/**
	 * 单位
	 */
	private String unitString;
	
	public ChineseMedicine(String name,List<String> bias,String number,String unit) {
		this.nameString = name;
		this.biasList = bias;
		this.numberString = number;
		this.unitString = unit;
	}
	
	public String getNameString(){
		return this.nameString;
	}
	public void setNameString(String string){
		this.nameString = string;
	}
	public List<String> getBiasList(){
		return this.biasList;
	}
	public void setBiasList(List<String> list){
		if(list == null || list.size() == 0){
			return;
		}
		this.biasList = list;
	}
	public String getNumberString(){
		return this.numberString;
	}
	public void setNumberString(String string){
		this.numberString = string;
	}
	public String getUnitString(){
		return this.unitString;
	}
	public void setUnitString(String string){
		this.unitString = string;
	}
	
	public String toString(){
		String result = "";
		
		if(this.biasList == null || this.biasList.size() == 0){
			// 没有别名
			result = this.nameString + "" + this.numberString + "" + this.unitString;
		}else{
			// 有一个或多个别名
			result = this.nameString + "(";
			int count = biasList.size();
			for(int i = 0; i < count;i++){
                            if(i == 0){
                                result += biasList.get(i);                            
                            }else{
                                result += "/" + biasList.get(i);
                            }				
			}
			result += ")" + this.numberString + "" + this.unitString;
			return result;
		}
		
		return result;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
