package com.um.myapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.um.classify.CWRelationMapping;
import com.um.data.DataBaseSetting;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.MedicineStatics;

@Controller
public class CNmedicinesController {
	
	@RequestMapping(value="cnmedicinestatis",method=RequestMethod.GET)
	public ModelAndView cnMedicineStatis(String batch){
		
		CWRelationMapping cwRelationMapping = new CWRelationMapping();// database process
		List<EHealthRecord> allRecords = cwRelationMapping.queryEhealthDataByCollection(DataBaseSetting.ehealthcollection);
		
		// 年度
		// 1.2 选取批次
		List<EHealthRecord> eHealthRecordsByBatch = null; // 符合某一批次的全部病历
		if(batch.equals("null")){
			eHealthRecordsByBatch = allRecords; // 全部病历，不区分批次
		}else{
			eHealthRecordsByBatch = new ArrayList<EHealthRecord>(); // 某一批次病历
			for(EHealthRecord e:allRecords){
				String batchString = "";
				if(e.getBatchString().contains(".")){
					batchString = e.getBatchString().substring(0, 4).trim();
				}else{
					batchString = e.getBatchString().trim();
				}
				if(batchString.equals(batch) || batchString == batch){
					eHealthRecordsByBatch.add(e);
				}
			}
		}
		
		
		int length = eHealthRecordsByBatch.size(); //病例数量
		
		List<String> medicineNamesList = new ArrayList<String>(); //中药名称í
		
		for(EHealthRecord e : eHealthRecordsByBatch){
			if(e.getChineseMedicines() != null && e.getChineseMedicines().size() > 0){
				for(ChineseMedicine c : e.getChineseMedicines()){
					medicineNamesList.add(c.getNameString());
				}
			}
		}

        // 根据全部的中药名称，进行统计中药数量
        HashMap<String, Integer> rHashMaps = MedicineStatics.staticsChineseMedicine(medicineNamesList);
        
        ModelAndView mv = new ModelAndView("statics");
        mv.addObject("medicinestatics", rHashMaps);
        mv.addObject("patientCount", length);
        return mv;
	}
}
