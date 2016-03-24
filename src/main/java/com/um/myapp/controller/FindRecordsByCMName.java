package com.um.myapp.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.util.EhealthUtil;
import com.um.util.MedicineByDescription;

@Controller
public class FindRecordsByCMName {
	
	private List<EHealthRecord> findedRecords;
	
	/**
	 * Find records by Chinese medicine name
	 * @return
	 */
	@RequestMapping(value="findrecordsbycmnane", method=RequestMethod.GET)
	public String findrecordsbycmnane(HttpServletRequest request, Model mv){
		List<EHealthRecord> targetList = new ArrayList<EHealthRecord>();
		
		// 1. parse the request parameters
		String batch = request.getParameter("batch").trim(); // batch 
		String medicine = request.getParameter("medicines").trim(); // request medicine
		String[] medicines = medicine.split(" ");
		
		
		// 2 get all records with same batch
		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch); // all record with same batch
		
		// 3. find the records by medicine name
		for (EHealthRecord eHealthRecord : eHealthRecordsByBatch) {
			if (eHealthRecord.getChineseMedicines() == null || eHealthRecord.getChineseMedicines().size() == 0) {
				continue;
			}
			int count = 0;
			for (ChineseMedicine cm : eHealthRecord.getChineseMedicines()) {
				for (String med : medicines) {
					if (cm.getNameString().equals(med)) {
						count++;
					}
				}
			}
			if (count == medicines.length) {
				// all medicine in this record
				eHealthRecord = EhealthUtil.encryptionRecord(eHealthRecord);
				targetList.add(eHealthRecord);
			}
			
		}
		
//		// 4. page
//		int pageNum = targetList.size() % 100 == 0 ? targetList.size() / 100 : (targetList.size() > 100 ? targetList.size() / 100 + 1 : 1);
//		if (targetList.size() > 100) {
//			targetList = targetList.subList(0, 100);
//		}
		
		// 4.return result
		mv.addAttribute("targetList", targetList);
		mv.addAttribute("medicines", medicine);
//		mv.addAttribute("pagenum", pageNum);
		return "findRecordByCMName";
	}
	
//	/**
//	 * Pages
//	 * @param id
//	 * @param model
//	 * @return
//	 */
//	@RequestMapping(value="pages/{id}", method=RequestMethod.GET)
//	public String page(@PathVariable String id, Model model){
//		System.out.println("id:" + id);
//		System.out.println("size: " + findedRecords.size());
//		int begin = 100 * (Integer.valueOf(id) - 1);
//		int end = 100 * Integer.valueOf(id) > findedRecords.size() ? findedRecords.size() : 100 * Integer.valueOf(id);
//	
//		List<EHealthRecord> result = findedRecords.subList(begin, end);
//		int pageNum = findedRecords.size() % 100 == 0 ? findedRecords.size() / 100 : (findedRecords.size() > 100 ? findedRecords.size() / 100 + 1 : 1);
//		
//		model.addAttribute("targetList", result);
//		model.addAttribute("pagenum", pageNum);
//		
//		return "findRecordByCMName";
//	}
}
