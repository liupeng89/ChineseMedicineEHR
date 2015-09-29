package com.um.classify;

import java.util.ArrayList;
import java.util.List;

import com.um.model.EHealthRecord;

public class DiagnosticsClassify {

	/**
	 *  中西医诊断分类： 根据不同的中西医诊断（关键字）进行分类
	 *  	
	 * @param args
	 */
	
	/*
	 *  分类描述，例如：肺癌气虚痰瘀互结
	 */
	private String diagString;
	
	/*
	 *  关键字
	 */
	private String[] keywrods;
	
	/*
	 *  关键字编码
	 */
	private String codeStrings;
	
	public String getCodeStrings() {
		return codeStrings;
	}

	public void setCodeStrings(String codeStrings) {
		this.codeStrings = codeStrings;
	}


	/*
	 *  病历统计
	 */
	private List<EHealthRecord> eHealthRecords;
	
	
	public DiagnosticsClassify(){
		
		diagString = "";
		keywrods = null;
		eHealthRecords = new ArrayList<EHealthRecord>();
	}
	
	public String toString(){
		
		return "类名：" + diagString;
	}
	
	
	public String getDiagString() {
		return diagString;
	}


	public void setDiagString(String diagString) {
		this.diagString = diagString;
	}


	public String[] getKeywrods() {
		return keywrods;
	}


	public void setKeywrods(String[] keywrods) {
		this.keywrods = keywrods;
	}


	public List<EHealthRecord> geteHealthRecords() {
		return eHealthRecords;
	}


	public void seteHealthRecords(List<EHealthRecord> eHealthRecords) {
		this.eHealthRecords = eHealthRecords;
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
