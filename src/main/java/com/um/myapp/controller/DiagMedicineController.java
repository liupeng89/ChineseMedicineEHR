package com.um.myapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mathworks.toolbox.javabuilder.MWException;
import com.um.data.DiagClassifyData;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.util.BasedOnRulePredict;
import com.um.util.DiagMedicineProcess;
import com.um.util.MachineLearningPredict;
import com.um.util.MedicineByDescription;

@Controller
public class DiagMedicineController {

	/**
	 *  Predict the Chinese medicines based on the user input info
	 *  	methods: 1. case statistics
	 *  			2. machine learning 
	 *  			3. based on the rules
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value="predictByStatisticAndMachine", method=RequestMethod.POST)
	public ModelAndView predictByStatisAndMachine(HttpServletRequest request) {
		
		ModelAndView mv = new ModelAndView("predictMedicine");
		
		/**
		 * 1. Parse the request parameters
		 */
		// 1.1 parse the request parameters
		Map<String, String> requestMap = MedicineByDescription.parseRequestParameter(request);
		
		// 1.2 get the diagnose, description, batch, threshold of machine learning
		String diagnose = requestMap.get("diagnose"); // diagnose
		String description = requestMap.get("description"); // description
		String batch = requestMap.get("batch");  // batch
		double threshold = Double.valueOf(requestMap.get("threshold"));  // threshold of machine learning
		
		// 1.3 formatted the description to output
		String descconvertString = MedicineByDescription.getFormatedDescirption(description);
		String descriptionString = diagnose + descconvertString;
		
		List<String> medicineListByStatis = new ArrayList<String>(); // predict medicines result
		/**
		 * 2. Case-base statistics to predict medicines
		 */
		// 2.1 statistics medicines larger than 90% records
		int outputnumber = 15; // the number of output medicine
		int similarnumber = 6; // similar record number
		
		// 2.2 get all records with same batch
		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch); // all record with same batch
		
		// 2.3 statistics name and number of medicines in this batch records
		Map<String, Integer> allMedicineMap = DiagMedicineProcess.statisEhealthMedicine(eHealthRecordsByBatch);
		
		// 2.4  find the medicines with percent larger than 90% 
		int allRecordsNum = eHealthRecordsByBatch.size(); // the number of this batch records
		double percent = 0.9; // the percent 
		
		List<String> medicineWithInevitable = DiagMedicineProcess.statisMedicineWithPercent(allMedicineMap, allRecordsNum, percent);
		if(medicineWithInevitable != null && medicineWithInevitable.size() > 0){
			medicineListByStatis.addAll(medicineWithInevitable); //the medicine with percent large than 90%
		}
		
		// 2.6 get similar records based on the description
		List<EHealthRecord> similaryRecords = MedicineByDescription.getSimilaryEHealthRecords(eHealthRecordsByBatch, diagnose, description);
		
		if (similaryRecords != null && similaryRecords.size() > 0) {
			// 2.7 statistic the medicines in the similar records
			Set<String> cnmedicineSet = DiagMedicineProcess.getMedicinesByDescription(description, similaryRecords);
			for (String med : medicineListByStatis) {
				if (!cnmedicineSet.contains(med)) {
					// remove the medicine from medicine list not in the cnmedicine set
					medicineListByStatis.remove(med);
				}
			}
			for (String cn : cnmedicineSet) {
				if (!medicineListByStatis.contains(cn)) {
					// add to result list
					medicineListByStatis.add(cn);
				}
			}
		}
		
		if (medicineListByStatis.size() > outputnumber) {
			medicineListByStatis = medicineListByStatis.subList(0, outputnumber);
		}
		
		// 2.7 Sort the medicine with same order with machine learning result
		List<String> medicineListByStatisticSorted = new ArrayList<String>();
		for( String s : DiagClassifyData.machineMedicine ){
			if (medicineListByStatis.contains(s)) {
				medicineListByStatisticSorted.add(s);
			}
		}
		
		// 2.8 get the similar records based on the input info, no more than 6 records
		if (similaryRecords.size() > similarnumber)  similaryRecords = similaryRecords.subList(0, similarnumber);
		
		/**
		 *  3. Machine learning to predict the medicines
		 */
		//  3.1 initial the input parameters of machine learning
		List<String> inputcode = MachineLearningPredict.parseDiagAndDesc(diagnose, description); // format the input parameters
		// 	3.2 predict the medicines based on the machine learning
		List<String> medicineListByMachine = MachineLearningPredict.predict(inputcode, threshold); // the predict result of machine learning
		
		/**
		 *  4. Based on the rules to predict the medicines
		 */
		List<String> medicineListByRules = BasedOnRulePredict.predictBasedOnRules(descriptionString);
		
		/**
		 * 5. all the predict result
		 */
		Map<String, Integer> comprehensiveResult = new HashMap<String, Integer>();
		List<String> comprehensiveResultOrder = new ArrayList<String>();
		for (String string : medicineListByStatisticSorted) {
			comprehensiveResult.put(string, 1);
		}
		for (String string : medicineListByMachine) {
			if (comprehensiveResult.get(string) != null) {
				int num = comprehensiveResult.get(string);
				comprehensiveResult.remove(string);
				comprehensiveResult.put(string, num+1);
			}else{
				comprehensiveResult.put(string, 1);
			}
		}
		for (String string : medicineListByRules) {
			if (comprehensiveResult.get(string) != null) {
				int num = comprehensiveResult.get(string);
				comprehensiveResult.remove(string);
				comprehensiveResult.put(string, num+1);
			}else {
				comprehensiveResult.put(string, 1);
			}
		}
		// sort the comprehensive
		Set<String> compKeySet = comprehensiveResult.keySet();
		for( String s : DiagClassifyData.machineMedicine ){
			if (compKeySet.contains(s)) {
				comprehensiveResultOrder.add(s);
			}
		}
		
		/**
		 * 5. Return the batch and other info
		 */
		mv.addObject("batch", batch);
		mv.addObject("medicineListByStatis", medicineListByStatisticSorted);
		mv.addObject("medicineListByMachine",medicineListByMachine);
		mv.addObject("medicineListByRules", medicineListByRules);
		mv.addObject("comprehensive", comprehensiveResult);
		mv.addObject("comprehensiveResultOrder", comprehensiveResultOrder);
		mv.addObject("diagnose", diagnose);
		mv.addObject("description", descconvertString);
		mv.addObject("similaryRecords",similaryRecords);
		
		
		return mv;
	}
	
	
	/**
	 *  Predict medicines based on the existed records
	 *  	methods:
	 *  			1. case-based statistics
	 *  			2. machine learning 
	 * @return
	 * @throws MWException 
	 */
	@RequestMapping("predicetByCase")
	public String predictByCaseController(HttpServletRequest request,Model model){
		// 1. get the input parameters
		String countString = request.getParameter("count"); // the order number of records
		
		int count = 0; // record order number
		double threshold = Double.valueOf(request.getParameter("threshold").trim()); // threshold of machine learning
		// 2. find all records with batch 2012
		List<EHealthRecord> allList = MedicineByDescription.getRecordsByBatch("2012");
		if ("".equals(countString)) {
			return "casePredictMedicine";
		}
		// 3. find the target record based on the conditions
		EHealthRecord targetRecord = null;
		
		if( countString.length() > 4 ){
			// the input info is the register number of record
			for( EHealthRecord e : allList ){
				if( e.getRegistrationno().equals(countString) ){
					targetRecord = e;
					break;
				}
				count++;
			}
		}else{
			// the input info is the order number of all records
			count = Integer.valueOf(countString); // order number
			count--;
			targetRecord = allList.get( count );
			
		}
		
		if(targetRecord == null){
			return "casePredictMedicine";
		}
		//4. the diagnose and description info of target record
		String diag = targetRecord.getChinesediagnostics();
		String description = targetRecord.getConditionsdescribed();
		String diagnose = "";
		String[] diagKeywords = DiagClassifyData.diagKeywords;
		for( String k : diagKeywords ){
			if(diag.matches(".*" + k + ".*")){
				diagnose += k + " ";
			}
		}
		// format the description of target record
		String formattedDescription = MedicineByDescription.formattedDescriptionByCount(description);
		// 5. the origin medicines in target record            
		List<String> orignMedicines = new ArrayList<String>();
		if( targetRecord.getChineseMedicines() != null && targetRecord.getChineseMedicines().size() > 0 ){
			for(ChineseMedicine c : targetRecord.getChineseMedicines()){
				orignMedicines.add(c.getNameString());
			}
		}
		
		// 6. sort the origin medicines with a fix order
		List<String> sortedList = new ArrayList<String>();
		
		for( String s : DiagClassifyData.machineMedicine ){
			for( String o : orignMedicines ){
				if( s == o || s.equals(o) ){
					sortedList.add(s);
				}
			}
		}
		
		// 7. predict medicines with machine learning 
		//  7.1 initial input parameters of machine learning
		List<String> inputcode = MachineLearningPredict.parseDiagAndDescByEhealthRecords(targetRecord);
		
		//  7.2 predict medicines with machine learning
		List<String> medicineListByMachine = MachineLearningPredict.predict(inputcode, threshold); // the result of machine learning
		
		// 8. calculate the accuracy
		double statisticsPercent = 0.0; // the accuracy of case-based
		double mechineLearningPercent = 0.0;  // the accuracy of machine learning
		
		int index = 0;
		
		statisticsPercent = 1.0 * orignMedicines.size() / orignMedicines.size(); //accuracy of case-based
		index = 0;
		
		for( String s : medicineListByMachine ){
			if( orignMedicines.contains(s) ){
				index++;
			}
		}
		mechineLearningPercent = 1.0 * index / orignMedicines.size(); // the accuracy of machine learning
		
		// 9. return result
		model.addAttribute("orignMedicines",sortedList);
		model.addAttribute("medicineListByStatis",sortedList);
		model.addAttribute("medicineListByMachine",medicineListByMachine);
		model.addAttribute("diagnose", diagnose);
		model.addAttribute("description", formattedDescription);
		model.addAttribute("statisticsPercent",statisticsPercent);
		model.addAttribute("mechineLearningPercent",mechineLearningPercent);
		model.addAttribute("regno",targetRecord.getRegistrationno());
		model.addAttribute("count",count + 1);
		return "casePredictMedicine";
	}
}
