package com.um.myapp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
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
import com.um.util.EhealthUtil;
import com.um.util.MachineLearningPredict;
import com.um.util.MedicineByDescription;

@Controller
public class DiagMedicineController {

	/**
	 *  基于用户输入预测处方
	 *  	预测方法：1、基于案例统计；2、基于机器学习；3、基于规则；
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value="predictByStatisticAndMachine", method=RequestMethod.POST)
	public ModelAndView predictByStatisAndMachine(HttpServletRequest request) {
		
		ModelAndView mv = new ModelAndView("predictMedicine");
		
		/**
		 * 1. 整理参数
		 */
		// 1.1 解析请求参数
		Map<String, String> requestMap = MedicineByDescription.parseRequestParameter(request);
		
		// 1.2 生成 诊断/描述
		String diagnose = requestMap.get("diagnose"); // 证型
		String description = requestMap.get("description"); // 症状
		String batch = requestMap.get("batch");  // 年度
		double threshold = Double.valueOf(requestMap.get("threshold"));  // 机器学习阈值
//		System.out.println("description:" + description);
		
		// 1.3 格式化描述输出，作为对描述的输出
		String descconvertString = MedicineByDescription.getFormatedDescirption(description);
		String descriptionString = diagnose + descconvertString;
//		System.out.println("new desc:" + descriptionString);
		
		/**
		 * 2. 基于统计的方法预测中药
		 */
		List<String> medicineListByStatis = MedicineByDescription.getMedicineByDiagAndDesc(batch,diagnose,description); // 根据描述，得出处方
		
		// Sort the medicine with same order with machine learning result
		List<String> medicineListByStatisticSorted = new ArrayList<String>();
		for( String s : DiagClassifyData.machineMedicine ){
			for( String o : medicineListByStatis ){
				if( s == o || s.equals(o) ){ medicineListByStatisticSorted.add(s); }
			}
		}
		
		// 2.1 提供相似病历  最多六个
		List<EHealthRecord> similaryRecords = MedicineByDescription.getSimilaryEHealthRecords(batch, diagnose, description);
		
		if (similaryRecords.size() > 6) { similaryRecords = similaryRecords.subList(0, 6); }
		
		/**
		 *  3. 基于机器学习的方法预测中药
		 */
		//  3.1 初始化输入参数
		List<String> inputcode = MachineLearningPredict.parseDiagAndDesc(diagnose, description); // 解析机器学习算法输入格式
		// 	3.2 机器学习预测   machine learning object
		List<String> medicineListByMachine = MachineLearningPredict.predict(inputcode, threshold); // 机器学习预测结果
		
		/**
		 *  4. Based on the rules
		 */
		List<String> medicineListByRules = BasedOnRulePredict.predictBasedOnRules(descriptionString);
		
		// 5. 获取批次
		List<String> batchList = DiagMedicineProcess.getBatch();
		mv.addObject("batchList", batchList);
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
	 *  基于现有病例来处方预测 ----序号， 结合统计 ＋ 机器学习
	 * @return
	 * @throws MWException 
	 */
	@RequestMapping("predicetByCase")
	public String predictByCaseController(HttpServletRequest request,Model model){
		// 1. 获取参数
		String countString = request.getParameter("count"); // 病例序号
		String thresholdString = request.getParameter("threshold");  // 机器学习阈值
		
		int count = 0; // 病例序号
		double threshold = Double.valueOf(thresholdString); // 机器学习阈值
		// 2. 查找病例
		
		// 1.1 读取数据库种病例数据
		Document conditions = new Document();
		conditions.append("ehealthrecord.batch", "2012");
		
		List<EHealthRecord> allList = EhealthUtil.getEhealthRecordListByConditions(conditions);
		
		int allcount = allList.size(); // 全部病例数量
		
		// 3. 目标病例
		EHealthRecord targetRecord = null;
		
		if( countString.length() > 4 ){
			// 输入的是挂号号
			for( EHealthRecord e : allList ){
				if( e.getRegistrationno().equals(countString) ){
					targetRecord = e;
					break;
				}
				count++;
			}
		}else{
			// 输入的是序号
			count = Integer.valueOf(countString); // 病例序号
			count--;
			targetRecord = allList.get( count );
			
		}
		
		if(targetRecord == null){
			model.addAttribute("allcount",allcount);
		}
		//4 . 目标病例的诊断和描述
		String diag = targetRecord.getChinesediagnostics();
		String description = targetRecord.getConditionsdescribed();
		String diagnose = "";
		String[] diagKeywords = DiagClassifyData.diagKeywords;
		for( String k : diagKeywords ){
			if(diag.matches(".*" + k + ".*")){
				diagnose += k + " ";
			}
		}
		// 格式化病症描述
		String formattedDescription = MedicineByDescription.formattedDescriptionByCount(description);
		System.out.println(formattedDescription);
		// 5. 原病例中药            
		List<String> orignMedicines = new ArrayList<String>();
		if( targetRecord.getChineseMedicines() != null && targetRecord.getChineseMedicines().size() > 0 ){
			for(ChineseMedicine c : targetRecord.getChineseMedicines()){
				orignMedicines.add(c.getNameString());
			}
		}
		
		// 原始中药进行排序
		List<String> sortedList = new ArrayList<String>();
		
		for( String s : DiagClassifyData.machineMedicine ){
			for( String o : orignMedicines ){
				if( s == o || s.equals(o) ){
					sortedList.add(s);
				}
			}
		}
		
		// 7. 机器学习预测
		//  7.1 初始化算法输入
		List<String> inputcode = MachineLearningPredict.parseDiagAndDescByEhealthRecords(targetRecord);
		System.out.println(inputcode.size());
		//  7.2 机器学习预测
		List<String> medicineListByMachine = MachineLearningPredict.predict(inputcode, threshold); // 机器学习预测结果
		
		// 8. 计算准确率
		double statisticsPercent = 0.0; // 案例统计准确率
		double mechineLearningPercent = 0.0;  // 机器学习准确率
		
		int index = 0;
		
		statisticsPercent = 1.0 * orignMedicines.size() / orignMedicines.size(); // 统计正确率
		index = 0;
		
		for( String s : medicineListByMachine ){
			if( orignMedicines.contains(s) ){
				index++;
			}
		}
		mechineLearningPercent = 1.0 * index / orignMedicines.size(); // 机器学习准确率
		
		// 9. 返回结果
		model.addAttribute("allcount",allcount);
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
