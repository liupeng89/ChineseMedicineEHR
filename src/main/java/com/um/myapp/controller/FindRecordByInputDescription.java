package com.um.myapp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.um.model.EHealthRecord;
import com.um.util.MedicineByDescription;

@Controller
public class FindRecordByInputDescription {
	
	/**
	 * Find records by input description
	 * @param request
	 * @return
	 */
	@RequestMapping(value="findrecordbyinputdescription", method=RequestMethod.POST)
	public ModelAndView findRecordByInputDescription(HttpServletRequest request){
		ModelAndView mv = new ModelAndView("findRecordByInputDescription");
		
		/**
		 * 1. Parse the request parameters
		 */
		// 1.1 parse the request parameters
		Map<String, String> requestMap = MedicineByDescription.parseRequestParameter(request);
		
		// 1.2 get the diagnose, description, batch, threshold of machine learning
		String diagnose = requestMap.get("diagnose"); // diagnose
		String description = requestMap.get("description"); // description
		String batch = requestMap.get("batch");  // batch
		
		// 1.3 formatted the description to output
		String descconvertString = MedicineByDescription.getFormatedDescirption(description);
		String descriptionString = diagnose + descconvertString;
		
		/**
		 * 2. Case-base statistics to predict medicines
		 */
		
		// 2.2 get all records with same batch
		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch); // all record with same batch
		
		// 2.6 get similar records based on the description
		List<EHealthRecord> similaryRecords = MedicineByDescription.getSimilaryEHealthRecords(eHealthRecordsByBatch, diagnose, description);
		
		mv.addObject("similaryRecords", similaryRecords);
		mv.addObject("description", descconvertString);
		mv.addObject("diagnose", diagnose);
		
		return mv;
	}
}
