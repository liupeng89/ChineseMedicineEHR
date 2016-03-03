package com.um.classify;

import java.util.ArrayList;
import java.util.List;

import com.um.model.EHealthRecord;

/**
 *  功能： 根据用户输入的病症，基于统计和规则，得到相对应的中药处方，输出；
 *  	输入：  用户的中医病症描述
 *  	输出：  对应的中药处方
 * @author lp
 *
 */
public class CnMedicinePrescription {

	private List<EHealthRecord> allEHealthRecords; // 全部病历
	
	public CnMedicinePrescription(List<EHealthRecord> eHealthRecords){
		this.allEHealthRecords = eHealthRecords;
	}
	
	/**
	 *  基于病症描述，得出中医处方
	 * @param diagString
	 * @return
	 */
	public List<String> getCnMedicines(String diagString){
		if(diagString == ""){
			return null;
		}
		List<String> medicieList = new ArrayList<String>();
		/*
		 *  1. 统计所有的中药处方，并排序
		 */
		
		/*
		 *  2. 找出出现概率大于90%的，作为结果 
		 */
		
		/*
		 * 	3. 累计后续中药，判断累加结果是否大于90%，根据判断规则输出
		 */
		
		/*
		 * 	4. 整理结果，并输出最终结果
		 */
		
		return medicieList;
	}
	
	// get and set
	public List<EHealthRecord> getAllEHealthRecords() {
		return allEHealthRecords;
	}

	public void setAllEHealthRecords(List<EHealthRecord> allEHealthRecords) {
		this.allEHealthRecords = allEHealthRecords;
	}
	
}
