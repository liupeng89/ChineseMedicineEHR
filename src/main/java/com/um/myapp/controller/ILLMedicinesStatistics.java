package com.um.myapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.MedicineStatics;
import com.um.util.EhealthUtil;

@Controller
public class ILLMedicinesStatistics {
	
	@RequestMapping(value = "illmedicinesstatistics", method = RequestMethod.POST)
	public String handleILLStatistics(HttpServletRequest request, Model model){
		String batch = request.getParameter("batch");
		String illstring = request.getParameter("ills");
		
		if(illstring.equals("")){
			return "statisticsByILL";
		}
		
		// !.Get all records
		Document conditions = new Document();
		if(!batch.equals("")){
			conditions.append("ehealthrecord.batch", batch.substring(0, 4));
		}
		final List<EHealthRecord> ehealthList = EhealthUtil.getEhealthRecordListByConditions(conditions);
		// 2. Find the ill records
		String[] illArray = illstring.split(" ");
		List<EHealthRecord> records = new ArrayList<EHealthRecord>();
		for(EHealthRecord e : ehealthList){
			int i = 0;
			for(String s : illArray){
				if(!e.getChinesediagnostics().matches(".*" + s + ".*")){
					break;
				}
				i++;
			}
			if(i == illArray.length-1){
				records.add(e);
			}
		}
		if(records == null || records.size() == 0){
			return "statisticsByILL";
		}
		
		// 3. Return results
		int length = records.size(); //病例数量
		
		List<String> medicineNamesList = new ArrayList<String>(); //中药名称í
		
		for(EHealthRecord e : records){
			if(e.getChineseMedicines() != null && e.getChineseMedicines().size() > 0){
				for(ChineseMedicine c : e.getChineseMedicines()){
					medicineNamesList.add(c.getNameString());
				}
			}
		}

        // 根据全部的中药名称，进行统计中药数量
        HashMap<String, Integer> rHashMaps = MedicineStatics.staticsChineseMedicine(medicineNamesList);
        model.addAttribute("batchList", batch);
        
        model.addAttribute("medicinestatics", rHashMaps);
        model.addAttribute("patientCount", length);
		
		return "statisticsByILL";
	}
}
