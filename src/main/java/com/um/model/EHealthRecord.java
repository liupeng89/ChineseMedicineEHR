package com.um.model;

import java.util.List;

public class EHealthRecord {

	/**
	 *  电子病历类
	 *  	主要包括：	1、医院信息
	 *  				2、病人信息
	 *  				3、病症描述
	 *  				4、中西医诊断
	 *  				5、处理/处方
	 *  
	 */
	
         
	
    private String id;
    
    /*
     *批次 
     */
    private String batchString;
    
	public String getBatchString() {
		return batchString;
	}

	public void setBatchString(String batchString) {
		this.batchString = batchString;
	}


	/*
	 * 挂号号
	 */
	private String registrationno;
	
	/*
	 * 医院
	 */
	private String hospital;
	
	/*
	 * 科别
	 */
	private String medicalservice;
	
	/*
	 * 时间
	 */
	private String date;
	
	/*
	 * 病人基本信息 
	 */
	private PatientInfo patientInfo;	
	/*
	 *病症描述 
	 */
	private String conditionsdescribed;
	
	/*
	 *中西医诊断 
	 */
	private String westerndiagnostics;//西医诊断
	private String chinesediagnostics;//中医诊断
	
	/*
	 * 处理/处方
	 */
	private String procesString; // 处理
	
	private List<WesternMedicine> westernMedicines;// 西药处方
	
	private List<ChineseMedicine> chineseMedicines;// 中药处方
	
	private String chineseProcess; // 中药处理
	
	/*
	 * 医师
	 */
	private String doctor;
	
	public String getChineseMedicinesToString(){
		if( chineseMedicines == null || chineseMedicines.size() == 0 ){
			return "";
		}
		int index = 0;
		String result = "";
		for(ChineseMedicine c : chineseMedicines ){
			if(index == chineseMedicines.size() - 1 ){
				result += c.getNameString();
			}else{
				result += c.getNameString() + ",";
			}
			index++;
		}
		return result;
	}
	
	/**
	 *  toString()
	 */
	public String toString(){
		String result = "";
		
		// 1、医院信息
		
		String separator = " ";
		
		result = "医院:"+ separator + this.hospital + separator +
				 "日期:"+ separator + this.date + separator +
				 "科别:"+ separator + this.medicalservice + separator;				 
		
		
		// 2、病人信息
		result += "[病人信息]:"+separator + this.patientInfo.toString() + separator;
		// 3、病症描述
		result += "描述:"+separator + this.conditionsdescribed + separator;
		// 4、中西医诊断
		result += "西医诊断:"+separator + this.westerndiagnostics + separator;
		result += "中医诊断:"+separator + this.chinesediagnostics + separator;
		// 5、处理
		result += "处理:" +separator+ this.procesString + separator;		
		// 6、中西医处方
		//   6.1 西药处方
		if(this.westernMedicines != null && this.westernMedicines.size() > 0){
			result += "[西药处方]:"+separator;
			for(WesternMedicine w : westernMedicines){
				result += w.toString() + separator;
			}
		}
		//   6.2 中药处方
		if(this.chineseMedicines != null && this.chineseMedicines.size() > 0){
			result += "[中药处方]:"+separator;
			for(ChineseMedicine c : this.chineseMedicines){
				if(c != null){
					result += c.toString() + separator;
				}
			}
		}
		
		// 7、中药处方操作
		result += "中药处理:"+separator + this.chineseProcess + separator;
		
		// 8. doctor
		result += "医师:"+separator + this.doctor;	
		
		return result;
	}
        
        /**
	 *  toString()
	 */
	public String toDisplay(){
		String result = "";
		
		// 1、医院信息
		
		result = "医院:" + this.hospital + "   " +
				 "日期: " + this.date + "   " +
				 "科别: " + this.medicalservice + "   ";				 
		
		
		// 2、病人信息
		result += "[病人信息]:" + this.patientInfo.toString() + "   ";
		// 3、病症描述
		result += "描述:" + this.conditionsdescribed + "   ";
		// 4、中西医诊断
		result += "西医诊断:" + this.westerndiagnostics + "   ";
		result += "中医诊断:" + this.chinesediagnostics + "   ";
		// 5、处理
		result += "处理:" + this.procesString + "   ";		
		// 6、中西医处方
		//   6.1 西药处方
		if(this.westernMedicines != null && this.westernMedicines.size() > 0){
			result += "[西药处方]:\n";
			for(WesternMedicine w : westernMedicines){
				result += w.toString() + "   ";
			}
		}
		//   6.2 中药处方
		if(this.chineseMedicines != null && this.chineseMedicines.size() > 0){
			result += "[中药处方]:\n";
			for(ChineseMedicine c : this.chineseMedicines){
				if(c != null){
					result += c.toString() + "  ";
//					System.out.println(c.toString());
				}
			}
		}
		
		// 7、中药处方操作
		
		// 8. doctor
		result += "医师: " + this.doctor;	
		
		return result;
	}
	
	public String getId(){
            return this.id;
        }
        
        public void setId(String idString){
            this.id = idString;
        }
	
	public String getRegistrationno(){
		return this.registrationno;
	}
	public void setRegistrationno(String string){
		this.registrationno = string;
	}
	
	public String getHospital(){
		return this.hospital;
	}
	public void setHospital(String string){
		this.hospital = string;
	}
	public String getMedicalservice(){
		return this.medicalservice;
	}
	public void setMedicalService(String string){
		this.medicalservice = string;
	}
	public String getDate(){
		return this.date;
	}
	public void setDate(String string){
		this.date = string;
	}
	public PatientInfo getPatientInfo(){
		return this.patientInfo;
	}
	public void setPatientInfo(PatientInfo pi){
		this.patientInfo = pi;
	}
	public String getConditionsdescribed(){
		return this.conditionsdescribed;
	}
	public void setConditionsdescribed(String string){
		this.conditionsdescribed = string;
	}
	public String getWesterndiagnostics(){
		return this.westerndiagnostics;
	}
	public void setWesterndiagnostics(String string){
		this.westerndiagnostics = string;
	}
	
	public String getChinesediagnostics(){
		return this.chinesediagnostics;
	}
	public void setChinesediagnostics(String string){
		this.chinesediagnostics = string;
	}
	public String getProcessString(){
		return this.procesString;
	}
	public void setProcessString(String string){
		this.procesString = string;
	}
	public List<WesternMedicine> getWesternMedicines(){
		return this.westernMedicines;
	}
	public void setWesternMedicines(List<WesternMedicine> list){
		this.westernMedicines = list;
	}
	public List<ChineseMedicine> getChineseMedicines(){
		return this.chineseMedicines;
	}
	public void setChineseMedicines(List<ChineseMedicine> list){
		this.chineseMedicines = list;
	}
	
	public String getChineseProcess(){
		return this.chineseProcess;
	}
	public void setChineseProcess(String string){
		this.chineseProcess = string;
	}
	
	public String getDoctor(){
		return this.doctor;
	}
	public void setDoctor(String string){
		this.doctor = string;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
