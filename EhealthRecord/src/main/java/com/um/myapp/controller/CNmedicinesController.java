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
	public ModelAndView cnMedicineStatis(){
		
		CWRelationMapping cwRelationMapping = new CWRelationMapping();// database process
//		List<EHealthRecord> allRecords = cwRelationMapping.queryEhealthData();
		List<EHealthRecord> allRecords = cwRelationMapping.queryEhealthDataByCollection(DataBaseSetting.ehealthcollection);
		
		int length = allRecords.size(); //病例数量
		
		List<String> medicineNamesList = new ArrayList<String>(); //中药名称í
		
		for(EHealthRecord e : allRecords){
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
