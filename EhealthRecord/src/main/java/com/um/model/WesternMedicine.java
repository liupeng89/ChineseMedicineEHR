package com.um.model;

public class WesternMedicine {

	/**
	 * 西药处方类
	 * 		主要包括： 组、名称、规格、使用方法、总量
	 * @param args
	 */
	
	/**
	 *  组
	 */
	private String groupString;
	/**
	 *名称
	 */
	private String nameString;
	/**
	 * 规格
	 */
	private String specifications;
	/**
	 * 用法
	 */
	private String usageString;
	/**
	 * 总量
	 */
	private String amountString;
        
        public WesternMedicine(){
        
        }
	
	public WesternMedicine(String group,String name,String specification,String usage,String amount) {
		this.groupString = group;
		this.nameString = name;
		this.specifications = specification;
		this.usageString = usage;
		this.amountString = amount;
	}
	
	public String getGroupString(){
		return this.groupString;
	}
	public void setGroupString(String string){
		this.groupString = string;
	}
	public String getNameString(){
		return this.nameString;
	}
	public void setNameString(String string){
		this.nameString = string;
	}
	public String getSpecifications(){
		return this.specifications;
	}
	public void setSpecifications(String string){
		this.specifications = string;
	}
	public String getUsageString(){
		return this.usageString;
	}
	public void setUsageString(String string){
		this.usageString = string;
	}
	public String getAmountString(){
		return this.amountString;
	}
	public void setAmountString(String string){
		this.amountString = string;
	}
	
	/**
	 *  toString()
	 */
	public String toString(){
		String resultString = "";
		resultString = "组:" + this.groupString +
					   "名称:" + this.nameString + 
					   "规格:" + this.specifications +
					   "用法:" + this.usageString +
					   "总量:" + this.amountString;
		return resultString;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
