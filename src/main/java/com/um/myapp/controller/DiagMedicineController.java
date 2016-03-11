package com.um.myapp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		
		/**
		 * 2. Case-base statistics to predict medicines
		 */
		// 2.1 predict the medicines based on the user input
		List<String> medicineListByStatis = MedicineByDescription.getMedicineByDiagAndDesc(batch,diagnose,description);
		
		// 2.2 Sort the medicine with same order with machine learning result
		List<String> medicineListByStatisticSorted = new ArrayList<String>();
		for( String s : DiagClassifyData.machineMedicine ){
			for( String o : medicineListByStatis ){
				if( s.equals(o) ){ 
					medicineListByStatisticSorted.add(s); 
				}
			}
		}
		
		// 2.3 get the similar records based on the input info, no more than 6 records
		List<EHealthRecord> similaryRecords = MedicineByDescription.getSimilaryEHealthRecords(batch, diagnose, description);
		if (similaryRecords.size() > 6)  similaryRecords = similaryRecords.subList(0, 6); 
		
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
		 * 5. Return the batch and other info
		 */
//		List<String> batchList = DiagMedicineProcess.getBatchString(); // batch info
//		mv.addObject("batchList", batchList);
		mv.addObject("batch", batch);
		mv.addObject("medicineListByStatis", medicineListByStatisticSorted);
		mv.addObject("medicineListByMachine",medicineListByMachine);
		mv.addObject("medicineListByRules", medicineListByRules);
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
//		Document conditions = new Document();
//		conditions.append("ehealthrecord.batch", "2012"); // the find conditions
//		
//		List<EHealthRecord> allList = EhealthUtil.getEhealthRecordListByConditions(conditions); // all records with batch 2012
		List<EHealthRecord> allList = MedicineByDescription.getRecordsByBatch("2012");
//		int allcount = allList.size(); // the count of batch 2012 records
		if ("".equals(countString)) {
//			model.addAttribute("allcount",allcount);
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
//			model.addAttribute("allcount",allcount);
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
		System.out.println(formattedDescription);
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
//		model.addAttribute("allcount",allcount);
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
