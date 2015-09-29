package com.um.classify;

import java.util.ArrayList;
import java.util.List;

import com.um.model.EHealthRecord;

/**
 *  根据中医病症描述，对病历进行分类
 * @author lp
 *
 */
public class CMDescriptionClassify {
	
	/*
	 * 描述信息
	 */
	private String descriptionString;
	/*
	 * 描述编码
	 */
	private String descriptionCode;
	/*
	 * 关键字组
	 */
	private String[] keywords;
	/*
	 *  病历统计
	 */
	private List<EHealthRecord> eHealthRecords;
	
	public CMDescriptionClassify(){
		this.descriptionString = "";
		this.descriptionCode = "";
		this.keywords = null;
		this.eHealthRecords = new ArrayList<EHealthRecord>();
	}

	public String getDescriptionString() {
		return descriptionString;
	}

	public void setDescriptionString(String descriptionString) {
		this.descriptionString = descriptionString;
	}

	public String getDescriptionCode() {
		return descriptionCode;
	}

	public void setDescriptionCode(String descriptionCode) {
		this.descriptionCode = descriptionCode;
	}

	public String[] getKeywords() {
		return keywords;
	}

	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}

	public List<EHealthRecord> geteHealthRecords() {
		return eHealthRecords;
	}

	public void seteHealthRecords(List<EHealthRecord> eHealthRecords) {
		this.eHealthRecords = eHealthRecords;
	}
	
}
