package com.um.myapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.MedicineStatics;
import com.um.util.MedicineByDescription;

@Controller
public class CNmedicinesController {
	
	@RequestMapping(value="cnmedicinestatis",method=RequestMethod.GET)
	public ModelAndView cnMedicineStatis(String batch){
		
		// 年度
		// 1.2 选取批次
		List<EHealthRecord> eHealthRecordsByBatch = null; // 符合某一批次的全部病历
		if(batch.equals("null")){
			eHealthRecordsByBatch = MedicineByDescription.getAllRecords();
		}else{
			eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch);
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
        
        ModelAndView mv = new ModelAndView("statisticsByCM");
        mv.addObject("medicinestatics", rHashMaps);
        mv.addObject("patientCount", length);
        return mv;
	}
}
