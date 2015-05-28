package com.um.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import predictum.Predictum;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWLogicalArray;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.um.data.DiagClassifyData;
import com.um.model.EHealthRecord;

/**
 *  Machine Learning predict medicines
 * @author heermaster
 *
 */
public class MachineLearningPredict {

	public static List<String> predict(List<String> inputcode){
		if( inputcode == null || inputcode.size() == 0 ){
			return null;
		}
		// machine learning object
		Predictum predictum = null;
		MWNumericArray x = null; /* Array of x values */
		Object[] y = null;
		
		try {
			predictum = new Predictum();
			
			int[] dims1 = { 1, 51 }; // x 输入 51项
			x = MWNumericArray.newInstance(dims1, MWClassID.DOUBLE,MWComplexity.REAL); // 生成x数组
			// 初始化 x 输入
			for(int i = 1; i <= 51; i++){
				x.set(i, Integer.valueOf(inputcode.get(i-1)));
			}
			// 机器预测
			y = predictum.predictum(1, x, 0.5);
		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MWLogicalArray yy = (MWLogicalArray) y[0];
		
		if(yy == null || yy.numberOfElements() == 0){
			return null;
		}
		
		int count = yy.numberOfElements(); // 161个
		// 输出结果
		String[] sortedmedicine = DiagClassifyData.machineMedicine;
		List<String> medicineListByMachine = new ArrayList<String>();
		for( int i = 0; i < count; i++ ){
			if( (Boolean) yy.get(i + 1) ){
				medicineListByMachine.add(sortedmedicine[i]);
			}
		}
		return medicineListByMachine;
	}
	
	/**
	 *  处理数据，生成符合机器学习输入格式的输入
	 *  
	 * @param diagnose
	 * @param description
	 * @return
	 */
	public static List<String> parseDiagAndDesc(String diagnose, String description){
		if( diagnose == "" || description == "" ){
			return null;
		}
		// 1. 证型
		String[] diagStrings = diagnose.split(" ");
		String diagString = "";
		for(String s:diagStrings){
			diagString += s + ",";
		}
		
		description = diagString + description;
		
		Map<String, String> descriptionCode = new HashMap<String, String>();
		//1， 读取关键字表---<部位 ， < 状态， [k1,k2,k3......] > >
		Map<String, HashMap<String, ArrayList<String>>> descriptionTableMap = DiagMedicineProcess.getDescriptionMap(DiagClassifyData.machineKeywords); //描述关键字列表
		
		Set<String> project = descriptionTableMap.keySet();
		HashMap<String, ArrayList<String>> desHashMap = null;
		for(String descString : project){
			String valueString = "0";
			desHashMap = descriptionTableMap.get(descString);
			Set<String> desSet = desHashMap.keySet();
			for(String s:desSet){
				ArrayList<String> keywordList = desHashMap.get(s);
				for(String key: keywordList){
					if(description.matches(".*" + key + ".*")){
						// 匹配
						valueString = s;
						break;
					}
				}
			}
			descriptionCode.put(descString, valueString);
		}
		
		List<String> inputcode = new ArrayList<String>(); // 最终结果－－需要按照顺序
		String[] sortedcode = DiagClassifyData.sortCode;
		for(String s : sortedcode){
			inputcode.add(descriptionCode.get(s));
		}
		return inputcode;
	}
	
	/**
	 *  根据病例数据，进行机器学习算法数据输入数据初始化
	 * @param e
	 * @return
	 */
	public static List<String> parseDiagAndDescByEhealthRecords( EHealthRecord e){
		if( e == null ){
			return null;
		}
		String diag = e.getChinesediagnostics();
		String description = e.getConditionsdescribed();
		
		// 描述中包含的关键字
		String[] descKeywords = DiagClassifyData.descKeywords;
		Map<String, String[]> descKeywordsMap = new HashMap<String, String[]>();
		for(String s : descKeywords){
			String[] splits = s.split(":");
			if(splits == null || splits.length != 2){
				continue;
			}
			String[] values = splits[1].split("\\|");
			if(values == null || values.length == 0){
				continue;
			}
			descKeywordsMap.put(splits[0], values);
		}
		
		// 诊断转换
		String[] diagKeywords = DiagClassifyData.diagKeywords;
		String targetDiagnose = "";
		for( String k : diagKeywords ){
			if(diag.matches(".*" + k + ".*")){
				targetDiagnose += k + ",";
			}
		}
		
		// 检查病例中的描述的关键字是否存在
		String targetDescription = ""; //目标病例的描述－－－－转变成代码
		Set<String> descKeywordSet = descKeywordsMap.keySet();// 全部项目
		for( String d : descKeywordSet){
			String[] values = descKeywordsMap.get(d);
			if( DiagMedicineProcess.checkDescriptionMatch(description, values)){
				//项目符合
				targetDescription += d + ",";
			}
		}
		
		String targetInputString = targetDiagnose + targetDescription;
		
		Map<String, String> descriptionCode = new HashMap<String, String>();
		//1， 读取关键字表---<部位 ， < 状态， [k1,k2,k3......] > >
		Map<String, HashMap<String, ArrayList<String>>> descriptionTableMap = DiagMedicineProcess.getDescriptionMap(DiagClassifyData.machineKeywords); //描述关键字列表
		
		Set<String> project = descriptionTableMap.keySet();
		HashMap<String, ArrayList<String>> desHashMap = null;
		for(String descString : project){
			String valueString = "0";
			desHashMap = descriptionTableMap.get(descString);
			Set<String> desSet = desHashMap.keySet();
			for(String s:desSet){
				ArrayList<String> keywordList = desHashMap.get(s);
				for(String key: keywordList){
					if(targetInputString.matches(".*" + key + ".*")){
						// 匹配
						valueString = s;
						break;
					}
				}
			}
			descriptionCode.put(descString, valueString);
		}
		
		List<String> inputcode = new ArrayList<String>();
		String[] sortedcode = DiagClassifyData.sortCode;
		for(String s : sortedcode){
			inputcode.add(descriptionCode.get(s));
		}
		return inputcode;
	}
}
