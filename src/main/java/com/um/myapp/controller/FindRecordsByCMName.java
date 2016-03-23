package com.um.myapp.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.util.EhealthUtil;
import com.um.util.MedicineByDescription;

@Controller
public class FindRecordsByCMName {
	
	/**
	 * Find records by Chinese medicine name
	 * @return
	 */
	@RequestMapping(value="findrecordsbycmnane", method=RequestMethod.GET)
	public ModelAndView findrecordsbycmnane(HttpServletRequest request){
		ModelAndView mv = new ModelAndView("findRecordByCMName");
		List<EHealthRecord> targetList = new ArrayList<EHealthRecord>();
		
		// 1. parse the request parameters
		String batch = request.getParameter("batch").trim(); // batch 
		String medicine = request.getParameter("medicines").trim(); // request medicine
		
		// 2 get all records with same batch
		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch); // all record with same batch
		
		// 3. find the records by medicine name
		for (EHealthRecord eHealthRecord : eHealthRecordsByBatch) {
			if (eHealthRecord.getChineseMedicines() == null || eHealthRecord.getChineseMedicines().size() == 0) {
				continue;
			}
			
			for (ChineseMedicine cm : eHealthRecord.getChineseMedicines()) {
				if (cm.getNameString().equals(medicine)) {
					eHealthRecord = EhealthUtil.encryptionRecord(eHealthRecord);
					targetList.add(eHealthRecord);
				}
			}
			
		}
		
		// 4.return result
		mv.addObject("targetList", targetList);
		return mv;
	}
}
