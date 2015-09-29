package com.um.myapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.um.classify.CWRelationMapping;
import com.um.data.DataBaseSetting;
import com.um.model.EHealthRecord;
import com.um.util.DiagMedicineProcess;

@Controller
public class CnMedicineStatisController {

	@RequestMapping(value="medicineProba",method=RequestMethod.GET)
	public ModelAndView cnMedicineStatis(String batch,String medicines){
		ModelAndView mv = new ModelAndView("cnmedicproba");
		if(medicines == ""){
			List<String> batchList = DiagMedicineProcess.getBatch();
//			System.out.println(batchList);
			mv.addObject("batchList", batchList);
			return mv;
		}
		System.out.println("[批次]:" +  batch);
		/**
		 * 1. 对中医处方进行统计，选择出现概率大于90%的中药作为结果输出；
		 */
		// 1.1 读取数据库种病例数据
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		List<EHealthRecord> allEHealthRecords = cwRelationMapping.queryEhealthDataByCollection(DataBaseSetting.ehealthcollection); // 全部病例
		
		// 1.2 选取批次
		List<EHealthRecord> eHealthRecordsByBatch = null; // 符合某一批次的全部病历
		
		if(batch.equals("null")){
			eHealthRecordsByBatch = allEHealthRecords; // 全部病历，不区分批次
		}else{
			eHealthRecordsByBatch = new ArrayList<EHealthRecord>(); // 某一批次病历
			for(EHealthRecord e:allEHealthRecords){
				String batchString = "";
				if( e.getBatchString().contains(".") ){
					batchString = e.getBatchString().substring(0,4).trim();
				}else{
					batchString = e.getBatchString().trim();
				}
				if(batchString.equals(batch) || batchString == batch){
					eHealthRecordsByBatch.add(e);
				}
			}
		}
		System.out.println(eHealthRecordsByBatch.size());
		if(eHealthRecordsByBatch == null || eHealthRecordsByBatch.size() == 0){
			
			List<String> batchList = DiagMedicineProcess.getBatch();
			mv.addObject("batchList", batchList);
			return mv;
		}
		
		// 1.3 统计中药
		Map<String, String> resultMap = DiagMedicineProcess.statisMedicProbability(medicines,eHealthRecordsByBatch);
		List<String> descriptionList = null;
		
		CWRelationMapping cMapping = new CWRelationMapping();		
		List<EHealthRecord> allRecrods = cMapping.queryEhealthData();
		
		if(resultMap.isEmpty() || resultMap == null){
			
			List<String> batchList = DiagMedicineProcess.getBatch();
//			System.out.println(batchList);
			mv.addObject("batchList", batchList);
			return mv;
		}
		
		Map<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
		
		Set<String> keySet = resultMap.keySet();
		
		if(resultMap.size() == 1){
			// 一组中药（单一或者两种）
			for(String s : keySet){
				String valueString = resultMap.get(s);
				ArrayList<String> valueList = new ArrayList<String>();
				if(valueString.contains("%")){
					//两味中药
					String[] vStrings = valueString.split("%");
					String[] unionStrings = vStrings[0].split("\\|");
					String[] mixStrings = vStrings[1].split("\\|");
					valueList.add(unionStrings[0]); //并集
					valueList.add(unionStrings[1]);//并集百分比
					valueList.add(mixStrings[0]);//交集
					valueList.add(mixStrings[1]);//交集百分比
				}else{
					//一味中药
					String[] vs = valueString.split("\\|"); // 区分交集和并集
//					System.out.println("[vs size] :" + vs.length);
					valueList.add(vs[0]); // 并集
					valueList.add(vs[1]); // 并集百分比
					descriptionList = DiagMedicineProcess.getDescriptionByMedicine(medicines, allRecrods);
				}
				result.put(s, valueList);
			}
		}else{
			//多味中药
			for(String s : keySet){
				String valueString = resultMap.get(s);
				ArrayList<String> valueList = new ArrayList<String>();
				String[] vs = valueString.split("%"); // 区分交集和并集
//				System.out.println("[vs size] :" + vs.length);
				valueList.add(vs[0].split("\\|")[0]); // 并集
				valueList.add(vs[0].split("\\|")[1]); // 并集百分比
				valueList.add(vs[1].split("\\|")[0]); // 交集
				valueList.add(vs[1].split("\\|")[1]); //交集百分比
				
				result.put(s, valueList);
			}
		}
		
		List<String> batchList = DiagMedicineProcess.getBatch();
//		System.out.println(batchList);
		mv.addObject("batchList", batchList);
		
		mv.addObject("results", result);
		mv.addObject("medicines", medicines);
		mv.addObject("descriptionlist", descriptionList);
		return mv;
	}
}
