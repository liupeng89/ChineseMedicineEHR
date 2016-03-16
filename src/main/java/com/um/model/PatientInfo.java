package com.um.model;

public class PatientInfo {
	
	/**
	 *  病人信息类
	 *  		主要包括：姓名、性别、年龄、职业、电话、联系人、地址
	 * @param args
	 */
	/*
	 * 姓名
	 */
	private String name;
	/*
	 * 年龄
	 */
	private String age;
	/*
	 * 性别
	 */
	private String gender;
	/*
	 * 职业
	 */
	private String profession;
	/*
	 * 手机号
	 */
	private String phoneNumber;
	/*
	 * 联系人
	 */
	private String contact;
	/*
	 * 地址
	 */
	private String address;
	
	public PatientInfo(){
		
	}
	
	/**
	 * tostring()
	 */
	public String toString(){
		String result = "";
		
		result = "姓名: " + this.name + "\n" +
				 " 年龄: " + this.age + "\n" + 
				 " 性别: " + this.gender + "\n" +
				 " 职业: " + this.profession + "\n" +
				 " 手机: " + this.phoneNumber + "\n" +
				 " 联系人: " + this.contact + "\n" +
				 " 地址: " + this.address;
		
		return result;
	}
	
	public String getName(){
		return this.name;
	}
	public void setName(String string){
		this.name = string;
	}
	public String getAge(){
		return this.age;
	}
	public void setAge(String string){
		this.age = string;
	}
	public String getGender(){
		return this.gender;
	}
	public void setGender(String string){
		this.gender = string;
	}
	public String getProfession(){
		return this.profession;
	}
	public void setProfession(String string){
		this.profession = string;
	}
	public String getPhoneNumber(){
		return this.phoneNumber;
	}
	public void setPhoneNumber(String string){
		this.phoneNumber = string;
	}
	public String getContact(){
		return this.contact;
	}
	public void setContact(String string){
		this.contact = string;
	}
	public String getAddress(){
		return this.address;
	}
	public void setAddress(String string){
		this.address = string;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
